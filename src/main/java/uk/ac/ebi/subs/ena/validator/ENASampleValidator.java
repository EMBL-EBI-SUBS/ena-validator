package uk.ac.ebi.subs.ena.validator;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.ena.errors.EnaDataErrorMessage;
import uk.ac.ebi.subs.ena.errors.EnaReferenceErrorMessage;
import uk.ac.ebi.subs.ena.processor.ENAProcessor;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.SampleValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.subs.ena.config.EnaValidatorQueues.ENA_SAMPLE_VALIDATION;

/**
 * This class responsible to do the ENA related validations.
 */
@Service
public class ENASampleValidator extends ENAValidator<Sample> {

    public ENASampleValidator(ENAProcessor enaProcessor, RabbitMessagingTemplate rabbitMessagingTemplate) {
        super(enaProcessor, rabbitMessagingTemplate);
    }

    @Override
    boolean isErrorRelevant(EnaReferenceErrorMessage enaReferenceErrorMessage, Sample entityToValidate) {
        return true; //errors are always relevant, there's only one thing in the submission
    }

    @Override
    boolean isErrorRelevant(EnaDataErrorMessage enaDataErrorMessage, Sample entityToValidate) {
        return true; //errors are always relevant, there's only one thing in the submission
    }

    @Override
    boolean isErrorRelevant(String message, Sample entityToValidate) {
        return true; //errors are always relevant, there's only one thing in the submission
    }

    /**
     * Do a validation for the sample submitted in the {@link SampleValidationMessageEnvelope}.
     * It produces a message according to the validation outcome.
     *
     * @param validationEnvelope {@link SampleValidationMessageEnvelope} that contains the sample to validate
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @RabbitListener(queues = ENA_SAMPLE_VALIDATION)
    public void validateSample(SampleValidationMessageEnvelope validationEnvelope) {
        final Sample sample = validationEnvelope.getEntityToValidate();
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();

        submissionEnvelope.setSubmission(createSubmission(validationEnvelope.getSubmissionId()));

        submissionEnvelope.getSamples().add(sample);
        final List<Sample> supportingSampleList = validationEnvelope.getSampleList().stream().
                filter(s -> validationEnvelope.getSubmissionId().equals(s.getSubmissionId())).
                map(s -> s.getBaseSubmittable()).
                collect(Collectors.toList());
        submissionEnvelope.getSamples().addAll(supportingSampleList);
        final List<SingleValidationResult> singleValidationResultList = validate(submissionEnvelope, sample);
        publishValidationMessage(validationEnvelope.getEntityToValidate(),
                singleValidationResultList,
                validationEnvelope.getValidationResultUUID(),
                validationEnvelope.getValidationResultVersion());
    }

}