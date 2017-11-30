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
        checkForEmptySingleValidationResult(singleValidationResultCollection,study);
        publishValidationMessage(study,singleValidationResultCollection,validationEnvelope.getValidationResultUUID(),validationEnvelope.getValidationResultVersion());
    }

}