package uk.ac.ebi.subs.ena.validator;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.ena.processor.ENAProcessor;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.subs.ena.config.EnaValidatorQueues.ENA_ASSAYDATA_VALIDATION;

/**
 * This class responsible to do the ENA related validations.
 */
@Service
public class ENAAssayDataValidator extends ENAValidator<AssayData> {

    public ENAAssayDataValidator(ENAProcessor enaProcessor, RabbitMessagingTemplate rabbitMessagingTemplate) {
        super(enaProcessor, rabbitMessagingTemplate);
    }

    /**
     * Do a validation for the assay data submitted in the {@link AssayDataValidationMessageEnvelope}.
     * It produces a message according to the validation outcome.
     *
     * @param validationEnvelope {@link AssayDataValidationMessageEnvelope} that contains the assay data to validate
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @RabbitListener(queues = ENA_ASSAYDATA_VALIDATION)
    public void validateAssayData(AssayDataValidationMessageEnvelope validationEnvelope) {
        final AssayData assayData = validationEnvelope.getEntityToValidate();
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();

        submissionEnvelope.setSubmission(createSubmission(validationEnvelope.getSubmissionId()));

        submissionEnvelope.getAssayData().add(assayData);

        boolean haveAssays = (validationEnvelope.getAssays() != null &&
                !validationEnvelope.getAssays().isEmpty());

        if (haveAssays) {
            Submittable<Assay> wrappedAssay = validationEnvelope.getAssays().iterator().next();

            if (validationEnvelope.getSubmissionId().equals( wrappedAssay.getSubmissionId())){
                submissionEnvelope.getAssays().add(wrappedAssay.getBaseSubmittable());
            }
        }

        List<SingleValidationResult> singleValidationResultList = validate(submissionEnvelope, assayData);

        singleValidationResultList = filterFileExistenceError(singleValidationResultList, assayData);

        publishValidationMessage(validationEnvelope.getEntityToValidate(),
                singleValidationResultList,
                validationEnvelope.getValidationResultUUID(),
                validationEnvelope.getValidationResultVersion());
    }

    List<SingleValidationResult> filterFileExistenceError(List<SingleValidationResult> singleValidationResultList,
                                                          AssayData submittable) {
        List<SingleValidationResult> filtererErrorList = singleValidationResultList.stream().filter(
                singleValidationResult -> {
                    String message = singleValidationResult.getMessage();

                    return !(message.startsWith("In run")
                            && message.contains("in the upload area"));
                }
        ).collect(Collectors.toList());

        if (filtererErrorList.size() == 0) {
            filtererErrorList = Collections.singletonList(createEmptySingleValidationResult(submittable));
        }

        return filtererErrorList;
    }
}