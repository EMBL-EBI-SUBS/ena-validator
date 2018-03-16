package uk.ac.ebi.subs.ena.validator;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.ena.processor.ENAProcessor;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.*;

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
     * Do a validation for the sample submitted in the {@link StudyValidationMessageEnvelope}.
     * It produces a message according to the validation outcome.
     *
     * @param validationEnvelope {@link StudyValidationMessageEnvelope} that contains the sample to validate
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @RabbitListener(queues = ENA_ASSAYDATA_VALIDATION)
    public void validateAssayData(AssayDataValidationMessageEnvelope validationEnvelope) {
        final AssayData assayData = validationEnvelope.getEntityToValidate();
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();
        submissionEnvelope.getAssayData().add(assayData);
        if (validationEnvelope.getAssay() != null && validationEnvelope.getSubmissionId().equals(validationEnvelope.getAssay().getSubmissionId())) {
            submissionEnvelope.getAssays().add(validationEnvelope.getAssay().getBaseSubmittable());
        }
        final List<SingleValidationResult> singleValidationResultList = validate(submissionEnvelope,assayData);
        publishValidationMessage(validationEnvelope.getEntityToValidate(),
                singleValidationResultList,
                validationEnvelope.getValidationResultUUID(),
                validationEnvelope.getValidationResultVersion());
    }
}