package uk.ac.ebi.subs.ena.validator;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Analysis;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.errors.EnaDataErrorMessage;
import uk.ac.ebi.subs.ena.errors.EnaReferenceErrorMessage;
import uk.ac.ebi.subs.ena.processor.ENAProcessor;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.AnalysisValidationEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.ac.ebi.subs.ena.config.EnaValidatorQueues.ENA_ANALYSIS_VALIDATION;

/**
 * This listener listens on the {@code ENA_ANALYSIS_VALIDATION} RabbitMQ queue,
 * executes validation of the published analysis object
 * and send the validation outcome to the validation service.
 */
@Service
public class ENAAnalysisValidator extends ENAValidator<Analysis> {

    private final Set<String> messagesToIgnore = new HashSet<>();

    public ENAAnalysisValidator(ENAProcessor enaProcessor, RabbitMessagingTemplate rabbitMessagingTemplate) {
        super(enaProcessor, rabbitMessagingTemplate);

        messagesToIgnore.add("Failed to validate analysis xml, error: Expected attribute: checksum_method in element FILE");
        messagesToIgnore.add("Sample in experiment is null");
        messagesToIgnore.add("Failed to validate analysis xml, error: Expected attribute: checksum in element FILE");
    }

    /**
     * Do a validation for the analysis submitted in the {@link AnalysisValidationEnvelope}.
     * It produces a message according to the validation outcome.
     *
     * @param validationEnvelope {@link AnalysisValidationEnvelope} that contains the assay data to validate
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @RabbitListener(queues = ENA_ANALYSIS_VALIDATION)
    public void validateAnalysis(AnalysisValidationEnvelope validationEnvelope) {
        final SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();
        submissionEnvelope.setSubmission(createSubmission(validationEnvelope.getSubmissionId()));

        final Analysis analysis = validationEnvelope.getEntityToValidate();
        submissionEnvelope.getAnalyses().add(analysis);

        addSupportingStudies(submissionEnvelope,validationEnvelope.getStudies());
        addSupportingSamples(submissionEnvelope,validationEnvelope.getSamples());

        List<SingleValidationResult> singleValidationResultList = validate(submissionEnvelope, analysis);

        publishValidationMessage(validationEnvelope.getEntityToValidate(),
                singleValidationResultList,
                validationEnvelope.getValidationResultUUID(),
                validationEnvelope.getValidationResultVersion());
    }

    private static void addSupportingStudies(SubmissionEnvelope submissionEnvelope, Collection<Submittable<Study>> wrappedStudies){
        if (wrappedStudies == null || wrappedStudies.isEmpty()) {
            return;
        }

        Collection<Study> studies = wrappedStudies
                .stream()
                .map(w -> w.getBaseSubmittable())
                .collect(Collectors.toList());

        submissionEnvelope.getStudies()
                .addAll(studies);
    }

    private static void addSupportingSamples(SubmissionEnvelope submissionEnvelope, Collection<Submittable<Sample>> wrappedSamples){
        if (wrappedSamples == null || wrappedSamples.isEmpty()) {
            return;
        }

        Collection<Sample> samples = wrappedSamples
                .stream()
                .map(w -> w.getBaseSubmittable())
                .collect(Collectors.toList());

        submissionEnvelope.getSamples()
                .addAll(samples);
    }

    @Override
    boolean isErrorRelevant(EnaReferenceErrorMessage enaReferenceErrorMessage, Analysis entityToValidate) {
        return !enaReferenceErrorMessage.getReferenceLocator().equals("SAMPLE_DESCRIPTOR");
    }

    @Override
    boolean isErrorRelevant(EnaDataErrorMessage enaDataErrorMessage, Analysis entityToValidate) {
        boolean entityTypeMatches = enaDataErrorMessage.getEnaEntityType().equals("analysis");
        boolean entityAliasMatches = enaDataErrorMessage.getAlias().equals(entityToValidate.getAlias());
        boolean entityTeamMatches = enaDataErrorMessage.getTeamName().equals(entityToValidate.getTeam().getName());
        boolean errorMessageIsNotAboutMissingFile = !enaDataErrorMessage.getMessage().contains("in the upload area");

        return entityTypeMatches &&
                entityAliasMatches &&
                entityTeamMatches &&
                errorMessageIsNotAboutMissingFile;
    }

    @Override
    boolean isErrorRelevant(String message, Analysis entityToValidate) {
        return  !(messagesToIgnore.contains(message));
    }
}
