package uk.ac.ebi.subs.ena.validator;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.ena.errors.EnaDataErrorMessage;
import uk.ac.ebi.subs.ena.errors.EnaReferenceErrorMessage;
import uk.ac.ebi.subs.ena.processor.ENAProcessor;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.subs.ena.config.EnaValidatorQueues.ENA_ASSAY_VALIDATION;

/**
 * This listener listens on the {@code ENA_ASSAY_VALIDATION} RabbitMQ queue,
 * executes validation of the published assay §object
 * and send the validation outcome to the validation service.
 */
@Service
public class ENAAssayValidator extends ENAValidator<Assay> {

    public ENAAssayValidator(ENAProcessor enaProcessor, RabbitMessagingTemplate rabbitMessagingTemplate) {
        super(enaProcessor, rabbitMessagingTemplate);
    }

    @Override
    boolean isErrorRelevant(EnaDataErrorMessage enaDataErrorMessage, Assay entityToValidate) {
        return enaDataErrorMessage.getEnaEntityType().equals("experiment");
    }

    @Override
    boolean isErrorRelevant(EnaReferenceErrorMessage enaReferenceErrorMessage, Assay entityToValidate) {
        return !enaReferenceErrorMessage.getReferenceLocator().equals("SAMPLE_DESCRIPTOR");
    }

    @Override
    boolean isErrorRelevant(String message, Assay entityToValidate) {
        return !message.equals("Sample in experiment is null");
    }

    /**
     * Do a validation for the assay submitted in the {@link AssayValidationMessageEnvelope}.
     * It produces a message according to the validation outcome.
     *
     * @param validationEnvelope {@link AssayValidationMessageEnvelope} that contains the assay to validate
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @RabbitListener(queues = ENA_ASSAY_VALIDATION)
    public void validateAssay(AssayValidationMessageEnvelope validationEnvelope) {
        final Assay assay = validationEnvelope.getEntityToValidate();
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();

        SampleAccessionAdjuster.fixSampleAccession(assay);

        submissionEnvelope.setSubmission(createSubmission(validationEnvelope.getSubmissionId()));

        submissionEnvelope.getAssays().add(assay);
        if (validationEnvelope.getStudy() != null && validationEnvelope.getSubmissionId().equals(validationEnvelope.getStudy().getSubmissionId())) {
            submissionEnvelope.getStudies().add(validationEnvelope.getStudy().getBaseSubmittable());
        }
        final List<Sample> supportingSampleList = validationEnvelope.getSampleList().stream().
                filter(s -> validationEnvelope.getSubmissionId().equals(s.getSubmissionId())).
                map(s -> s.getBaseSubmittable()).
                collect(Collectors.toList());
        submissionEnvelope.getSamples().addAll(supportingSampleList);
        final List<SingleValidationResult> singleValidationResultList = validate(submissionEnvelope, assay);
        publishValidationMessage(validationEnvelope.getEntityToValidate(),
                singleValidationResultList,
                validationEnvelope.getValidationResultUUID(),
                validationEnvelope.getValidationResultVersion());
    }
}