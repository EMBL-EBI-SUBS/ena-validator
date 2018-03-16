package uk.ac.ebi.subs.ena.validator;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.ena.processor.ENAProcessor;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.*;

import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.subs.ena.config.EnaValidatorQueues.ENA_ASSAY_VALIDATION;

/**
 * This class responsible to do the ENA related validations.
 */
@Service
public class ENAAssayValidator extends ENAValidator<Assay> {

    public ENAAssayValidator(ENAProcessor enaProcessor, RabbitMessagingTemplate rabbitMessagingTemplate) {
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
    @RabbitListener(queues = ENA_ASSAY_VALIDATION)
    public void validateSample(AssayValidationMessageEnvelope validationEnvelope) {
        final Assay assay = validationEnvelope.getEntityToValidate();
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();
        submissionEnvelope.getAssays().add(assay);
        if (validationEnvelope.getStudy() != null && validationEnvelope.getSubmissionId().equals(validationEnvelope.getStudy().getSubmissionId())) {
            submissionEnvelope.getStudies().add(validationEnvelope.getStudy().getBaseSubmittable());
        }
        final List<Sample> supportingSampleList = validationEnvelope.getSampleList().stream().
                filter(s -> validationEnvelope.getSubmissionId().equals(s.getSubmissionId())).
                map(s -> s.getBaseSubmittable()).
                collect(Collectors.toList());
        submissionEnvelope.getSamples().addAll(supportingSampleList);
        final List<SingleValidationResult> singleValidationResultList = validate(submissionEnvelope,assay);
        publishValidationMessage(validationEnvelope.getEntityToValidate(),
                singleValidationResultList,
                validationEnvelope.getValidationResultUUID(),
                validationEnvelope.getValidationResultVersion());
    }

}