package uk.ac.ebi.subs.ena.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.submittable.BaseSubmittable;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.ena.errors.EnaDataErrorMessage;
import uk.ac.ebi.subs.ena.errors.EnaErrorMessageHelper;
import uk.ac.ebi.subs.ena.errors.EnaReferenceErrorMessage;
import uk.ac.ebi.subs.ena.processor.ENAProcessor;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static uk.ac.ebi.subs.ena.config.EnaValidatorRoutingKeys.EVENT_VALIDATION_ERROR;
import static uk.ac.ebi.subs.ena.config.EnaValidatorRoutingKeys.EVENT_VALIDATION_SUCCESS;

/**
 * This is the base class for ENA related validation.
 * Validation for submittable objects (like sample, study, assay, assay data, analysis)
 * are extending this base validator to add their specific validation code.
 *
 * Created by karoly on 14/06/2017.
 */
@Service
public abstract class ENAValidator<T extends BaseSubmittable> {

    Logger logger = LoggerFactory.getLogger(ENAValidator.class);
    ENAProcessor enaProcessor;
    RabbitMessagingTemplate rabbitMessagingTemplate;

    EnaErrorMessageHelper enaErrorMessageHelper = new EnaErrorMessageHelper();

    public ENAValidator(ENAProcessor enaProcessor, RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.enaProcessor = enaProcessor;
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    protected List<SingleValidationResult> validate(SubmissionEnvelope submissionEnvelope, T entityToValidate) {
        logger.info("Validating " + entityToValidate);
        List<SingleValidationResult> rawResults = enaProcessor.process(submissionEnvelope);

        List<SingleValidationResult> filteredValidationResults = filterValidationResults(rawResults, entityToValidate);
        if (filteredValidationResults.isEmpty()) {
            return Collections.singletonList(createEmptySingleValidationResult(entityToValidate));
        } else {

            for (SingleValidationResult result : filteredValidationResults) {
                result.setEntityUuid(entityToValidate.getId());
            }

            return filteredValidationResults;
        }
    }

    private List<SingleValidationResult> filterValidationResults(List<SingleValidationResult> rawValidationResults, T entityToValidate) {
        List<SingleValidationResult> passedMessages = new ArrayList<>();

        for (SingleValidationResult validationResult : rawValidationResults) {

            // handle well structured error messages, then less well structured
            if (enaErrorMessageHelper.isDataError(validationResult)) {
                if (isDataErrorRelevant(validationResult, entityToValidate)) {
                    passedMessages.add(validationResult);
                }
            } else if (enaErrorMessageHelper.isReferenceError(validationResult)) {
                if (isReferenceErrorRelevant(validationResult, entityToValidate)) {
                    passedMessages.add(validationResult);
                }
            } else if (isErrorRelevant(validationResult.getMessage(), entityToValidate)) {
                passedMessages.add(validationResult);
            }
        }

        return passedMessages;
    }

    private boolean isDataErrorRelevant(SingleValidationResult validationResult, T entityToValidate) {
        EnaDataErrorMessage enaDataErrorMessage = enaErrorMessageHelper.parseDataError(validationResult);

        return isErrorRelevant(enaDataErrorMessage, entityToValidate);
    }

    private boolean isReferenceErrorRelevant(SingleValidationResult validationResult, T entityToValidate) {
        EnaReferenceErrorMessage enaReferenceErrorMessage = enaErrorMessageHelper.parseReferenceError(validationResult);

        return isErrorRelevant(enaReferenceErrorMessage, entityToValidate);
    }

    abstract boolean isErrorRelevant(EnaDataErrorMessage enaDataErrorMessage, T entityToValidate);

    abstract boolean isErrorRelevant(EnaReferenceErrorMessage enaReferenceErrorMessage, T entityToValidate);

    abstract boolean isErrorRelevant(String message, T entityToValidate);

    Submission createSubmission(String submissionId) {
        Submission submission = new Submission();
        submission.setId(submissionId);
        return submission;
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

    SingleValidationResult createEmptySingleValidationResult(Submittable submittable) {
        SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Ena, submittable.getId());
        singleValidationResult.setValidationStatus(SingleValidationResultStatus.Pass);
        return singleValidationResult;
    }

}
