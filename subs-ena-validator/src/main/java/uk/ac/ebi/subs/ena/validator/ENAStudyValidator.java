package uk.ac.ebi.subs.ena.validator;

import org.joda.time.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.processor.ENAProcessorContainerService;
import uk.ac.ebi.subs.ena.processor.ENAStudyProcessor;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationAuthor;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationStatus;
import uk.ac.ebi.subs.validator.messaging.Queues;
import java.util.List;
import java.util.UUID;

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
     * Do a validation for the sample submitted in the {@link ValidationMessageEnvelope}.
     * It produces a message according to the validation outcome.
     *
     * @param validationEnvelope {@link ValidationMessageEnvelope} that contains the sample to validate
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Transactional
    @RabbitListener(queues = Queues.ENA_STUDY_VALIDATION)
    public void validateStudy(ValidationMessageEnvelope<Study> validationEnvelope) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
            Interval interval = new Interval(new Instant().getMillis(),study.getReleaseDate().getTime());
            final Days days = Days.daysBetween(new DateTime(), new DateTime(study.getReleaseDate()));
            if (days.getDays() >= intevalDays) {
                SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Ena,study.getId());
                singleValidationResult.setUuid(UUID.randomUUID().toString());
                singleValidationResult.setValidationStatus(ValidationStatus.Error);
                singleValidationResult.setMessage(String.format("Release date %s must not exceed two years from the present date",study.getReleaseDate()));
                singleValidationResultCollection.add(singleValidationResult);
            }
        } else {
            SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Ena,study.getId());
            singleValidationResult.setUuid(UUID.randomUUID().toString());
            singleValidationResult.setValidationStatus(ValidationStatus.Error);
            singleValidationResult.setMessage("A release date for a study must be provided");
            singleValidationResultCollection.add(singleValidationResult);
        }

    }
}