package uk.ac.ebi.subs.ena.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.submittable.BaseSubmittable;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.ena.processor.ENAProcessor;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static uk.ac.ebi.subs.ena.config.EnaValidatorRoutingKeys.EVENT_VALIDATION_ERROR;
import static uk.ac.ebi.subs.ena.config.EnaValidatorRoutingKeys.EVENT_VALIDATION_SUCCESS;

/**
 * Created by karoly on 14/06/2017.
 */
@Service
public abstract class ENAValidator<T extends BaseSubmittable> {

    Logger logger = LoggerFactory.getLogger(ENAValidator.class);
    ENAProcessor enaProcessor;
    RabbitMessagingTemplate rabbitMessagingTemplate;

    public ENAValidator(ENAProcessor enaProcessor, RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.enaProcessor = enaProcessor;
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    protected List<SingleValidationResult> validate (SubmissionEnvelope submissionEnvelope, T entityToValidate) {
        Submission submission = new Submission();
        submission.setTeam(submission.getTeam());
        submission.setId(entityToValidate.getAlias());
        submissionEnvelope.setSubmission(submission);
        List<SingleValidationResult> singleValidationResultList = new ArrayList<>();
        logger.info("Validating " + entityToValidate);
        singleValidationResultList = enaProcessor.process(submissionEnvelope);
        if (singleValidationResultList.isEmpty()) {
            return Collections.singletonList(createEmptySingleValidationResult(entityToValidate));
        } else {
            return singleValidationResultList;
        }

    }

    void publishValidationMessage(Submittable submittable, List<SingleValidationResult> singleValidationResultCollection,
                                  String validationResultUuid, int validationResultVersion) {

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = new SingleValidationResultsEnvelope(
                singleValidationResultCollection, validationResultVersion, validationResultUuid, ValidationAuthor.Ena
        );

        if (!hasValidationError(singleValidationResultCollection)) {
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_VALIDATION_SUCCESS, singleValidationResultsEnvelope);

            logger.info("Validation successful for {} entity with id: {}", submittable.getClass().getSimpleName(), submittable.getId());
        } else {
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_VALIDATION_ERROR, singleValidationResultsEnvelope);

            logger.info("Validation erred for {} entity with id: {}", submittable.getClass().getSimpleName(), submittable.getId());
        }

    }

    boolean hasValidationError(List<SingleValidationResult> validationResults) {
        SingleValidationResult errorValidationResult = validationResults.stream().filter(
                validationResult -> validationResult.getValidationStatus() == SingleValidationResultStatus.Error)
                .findAny()
                .orElse(null);

        return errorValidationResult != null;
    }

    void checkForEmptySingleValidationResult (List<SingleValidationResult> singleValidationResultList, Submittable submittable) {
        if (singleValidationResultList.isEmpty()) singleValidationResultList.add(createEmptySingleValidationResult(submittable));
    }

    SingleValidationResult createEmptySingleValidationResult (Submittable submittable) {
        SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Ena, submittable.getId());
        singleValidationResult.setValidationStatus(SingleValidationResultStatus.Pass);
        return singleValidationResult;
    }

}
