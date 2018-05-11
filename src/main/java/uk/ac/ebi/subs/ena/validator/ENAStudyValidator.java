package uk.ac.ebi.subs.ena.validator;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.errors.EnaDataErrorMessage;
import uk.ac.ebi.subs.ena.errors.EnaReferenceErrorMessage;
import uk.ac.ebi.subs.ena.processor.ENAProcessor;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;

import java.util.List;

import static uk.ac.ebi.subs.ena.config.EnaValidatorQueues.ENA_STUDY_VALIDATION;

/**
 * This class responsible to do the ENA related validations.
 */
@Service
public class ENAStudyValidator extends ENAValidator<Study> {

    public ENAStudyValidator(ENAProcessor enaProcessor, RabbitMessagingTemplate rabbitMessagingTemplate) {
        super(enaProcessor, rabbitMessagingTemplate);
    }

    @Override
    boolean isErrorRelevant(EnaReferenceErrorMessage enaReferenceErrorMessage, Study entityToValidate) {
        return true; //errors are always relevant, there's only one thing in the submission
    }

    @Override
    boolean isErrorRelevant(EnaDataErrorMessage enaDataErrorMessage, Study entityToValidate) {
        return true; //errors are always relevant, there's only one thing in the submission
    }

    @Override
    boolean isErrorRelevant(String message, Study entityToValidate) {
        return true; //errors are always relevant, there's only one thing in the submission
    }

    /**
     * Do a validation for the study submitted in the {@link StudyValidationMessageEnvelope}.
     * It produces a message according to the validation outcome.
     *
     * @param validationEnvelope {@link StudyValidationMessageEnvelope} that contains the study to validate
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @RabbitListener(queues = ENA_STUDY_VALIDATION)
    public void validateStudy(StudyValidationMessageEnvelope validationEnvelope) {
        final Study study = validationEnvelope.getEntityToValidate();
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();

        submissionEnvelope.setSubmission(createSubmission(validationEnvelope.getSubmissionId()));

        submissionEnvelope.getStudies().add(study);
        final List<SingleValidationResult> singleValidationResultList = validate(submissionEnvelope, study);
        publishValidationMessage(validationEnvelope.getEntityToValidate(),
                singleValidationResultList,
                validationEnvelope.getValidationResultUUID(),
                validationEnvelope.getValidationResultVersion());
    }

}