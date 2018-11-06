package uk.ac.ebi.subs.ena.validator;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.ena.errors.EnaDataErrorMessage;
import uk.ac.ebi.subs.ena.errors.EnaReferenceErrorMessage;
import uk.ac.ebi.subs.ena.processor.ENAProcessor;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.List;

import static uk.ac.ebi.subs.ena.config.EnaValidatorQueues.ENA_ASSAYDATA_VALIDATION;

/**
 * This listener listens on the {@code ENA_ASSAYDATA_VALIDATION} RabbitMQ queue,
 * executes validation of the published assay data object
 * and send the validation outcome to the validation service.
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

            SampleAccessionAdjuster.fixSampleAccession(wrappedAssay.getBaseSubmittable());

            if (validationEnvelope.getSubmissionId().equals(wrappedAssay.getSubmissionId())) {
                submissionEnvelope.getAssays().add(wrappedAssay.getBaseSubmittable());
            }
        }

        List<SingleValidationResult> singleValidationResultList = validate(submissionEnvelope, assayData);

        publishValidationMessage(validationEnvelope.getEntityToValidate(),
                singleValidationResultList,
                validationEnvelope.getValidationResultUUID(),
                validationEnvelope.getValidationResultVersion());
    }


    @Override
    boolean isErrorRelevant(EnaReferenceErrorMessage enaReferenceErrorMessage, AssayData entityToValidate) {
        return !enaReferenceErrorMessage.getReferenceLocator().equals("SAMPLE_DESCRIPTOR");
    }

    @Override
    boolean isErrorRelevant(EnaDataErrorMessage enaDataErrorMessage, AssayData entityToValidate) {
        boolean entityTypeMatches = enaDataErrorMessage.getEnaEntityType().equals("run");
        boolean entityAliasMatches = enaDataErrorMessage.getAlias().equals(entityToValidate.getAlias());
        boolean entityTeamMatches = enaDataErrorMessage.getTeamName().equals(entityToValidate.getTeam().getName());
        boolean errorMessageIsNotAboutMissingFile = !enaDataErrorMessage.getMessage().contains("in the upload area");

        return entityTypeMatches &&
                entityAliasMatches &&
                entityTeamMatches &&
                errorMessageIsNotAboutMissingFile;
    }

    @Override
    boolean isErrorRelevant(String message, AssayData entityToValidate) {
        if (message.equals("Sample in experiment is null")) {
            return false;
        }

        return true;
    }
}