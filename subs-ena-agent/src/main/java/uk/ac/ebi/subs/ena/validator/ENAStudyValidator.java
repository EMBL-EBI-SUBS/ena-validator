package uk.ac.ebi.subs.ena.validator;

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
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.messaging.Queues;
import java.util.List;

/**
 * This class responsible to do the ENA related validations.
 */
@Service
public class ENAStudyValidator implements ENAValidator {

    private static final Logger logger = LoggerFactory.getLogger(ENAStudyValidator.class);

    @Autowired
    ENAStudyProcessor enaStudyProcessor;

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

    public ENAStudyValidator(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter,
                             ENAProcessorContainerService enaProcessorContainerService) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
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
        publishValidationMessage(study,singleValidationResultCollection,validationEnvelope.getValidationResultUUID(),validationEnvelope.getValidationResultVersion());
    }
}