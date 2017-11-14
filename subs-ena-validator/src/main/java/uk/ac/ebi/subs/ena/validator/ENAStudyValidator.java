package uk.ac.ebi.subs.ena.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.processor.ENAProcessorContainerService;
import uk.ac.ebi.subs.ena.processor.ENAStudyProcessor;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static uk.ac.ebi.subs.ena.config.EnaValidatorQueues.ENA_STUDY_VALIDATION;

/**
 * This class responsible to do the ENA related validations.
 */
@Service
public class ENAStudyValidator implements ENAValidator {

    private static final Logger logger = LoggerFactory.getLogger(ENAStudyValidator.class);

    @Autowired
    ENAStudyProcessor enaStudyProcessor;

    public static final int RELEASE_DATE_INTERVAL_DAYS = 730;

    public ENAStudyProcessor getEnaStudyProcessor() {
        return enaStudyProcessor;
    }

    public void setEnaStudyProcessor(ENAStudyProcessor eNAStudyProcessor) {
        this.enaStudyProcessor = eNAStudyProcessor;
    }

    ENAProcessorContainerService enaProcessorContainerService;

    RabbitMessagingTemplate rabbitMessagingTemplate;

    @Override
    public void setRabbitMessagingTemplate(RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    @Override
    public RabbitMessagingTemplate getRabbitMessagingTemplate() {
        return rabbitMessagingTemplate;
    }

    public ENAStudyValidator(RabbitMessagingTemplate rabbitMessagingTemplate,
                             ENAProcessorContainerService enaProcessorContainerService) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.enaProcessorContainerService = enaProcessorContainerService;
    }


    /**
     * Do a validation for the sample submitted in the {@link StudyValidationMessageEnvelope}.
     * It produces a message according to the validation outcome.
     *
     * @param validationEnvelope {@link StudyValidationMessageEnvelope} that contains the sample to validate
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @RabbitListener(queues = ENA_STUDY_VALIDATION)
    public void validateStudy(StudyValidationMessageEnvelope validationEnvelope) {
        final Study study = validationEnvelope.getEntityToValidate();
        final List<SingleValidationResult> singleValidationResultCollection = executeSubmittableValidation(study,enaStudyProcessor );
        checkReleaseDate(study,singleValidationResultCollection);
        checkForEmptySingleValidationResult(singleValidationResultCollection,study);
        publishValidationMessage(study,singleValidationResultCollection,validationEnvelope.getValidationResultUUID(),validationEnvelope.getValidationResultVersion());
    }

    void checkReleaseDate(Study study, List<SingleValidationResult> singleValidationResultCollection) {
        checkReleaseDate(study,singleValidationResultCollection,RELEASE_DATE_INTERVAL_DAYS);
    }

    void checkReleaseDate(Study study, List<SingleValidationResult> singleValidationResultCollection, int intevalDays) {
        if (study.getReleaseDate() != null) {
            final Instant instant = Instant.now();
            final long releaseDateInMillis = Date.from(study.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()).toInstant().toEpochMilli();
            if ( releaseDateInMillis > instant.toEpochMilli()) {
                final long daysBetween = DAYS.between(LocalDate.now(), study.getReleaseDate());
                if (daysBetween >= intevalDays) {
                    SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Ena, study.getId());
                    singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
                    singleValidationResult.setMessage(String.format("Release date %s must not exceed two years from the present date", study.getReleaseDate()));
                    singleValidationResultCollection.add(singleValidationResult);
                }
            }
        } else {
            SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Ena,study.getId());
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
            singleValidationResult.setMessage("A release date for a study must be provided");
            singleValidationResultCollection.add(singleValidationResult);
        }

    }
}