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
import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.ValidationMessage;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.processor.ENAProcessorContainerService;
import uk.ac.ebi.subs.ena.processor.ENAStudyProcessor;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.messaging.Queues;

import java.util.Collection;

/**
 * This class responsible to do the ENA related validations.
 */
@Service
public class EnaAgentStudyValidator implements EnaAgentValidator {

    private static final Logger logger = LoggerFactory.getLogger(EnaAgentStudyValidator.class);

    @Autowired
    ENAStudyProcessor enaStudyProcessor;

    public ENAStudyProcessor getEnaStudyProcessor() {
        return enaStudyProcessor;
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

    public EnaAgentStudyValidator(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter,
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

        Collection<ValidationMessage<Origin>> validationMessages = executeSubmittableValidation(study, enaStudyProcessor);

        publishValidationMessage(study, validationMessages, validationEnvelope.getValidationResultUUID());
    }
}