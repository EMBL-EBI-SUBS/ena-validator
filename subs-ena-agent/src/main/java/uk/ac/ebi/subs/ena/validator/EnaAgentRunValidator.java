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
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.ena.processor.ENAProcessorContainerService;
import uk.ac.ebi.subs.ena.processor.ENARunProcessor;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.messaging.Queues;

import java.util.Collection;

/**
 * This class responsible to do the ENA related validations.
 */
@Service
public class EnaAgentRunValidator implements EnaAgentValidator {

    private static final Logger logger = LoggerFactory.getLogger(EnaAgentRunValidator.class);

    @Autowired
    ENARunProcessor enaRunProcessor;

    public ENARunProcessor getEnaRunProcessor() {
        return enaRunProcessor;
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

    public EnaAgentRunValidator(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter,
                                ENAProcessorContainerService enaProcessorContainerService) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
        this.enaProcessorContainerService = enaProcessorContainerService;
    }


    /**
     * Do a validation for the {@link AssayData} submitted in the {@link ValidationMessageEnvelope}.
     * It produces a message according to the validation outcome.
     *
     * @param validationEnvelope {@link ValidationMessageEnvelope} that contains the assay data to validate
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Transactional
    @RabbitListener(queues = Queues.ENA_ASSAYDATA_VALIDATION)
    public void validateAssayData(ValidationMessageEnvelope<AssayData> validationEnvelope) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        final AssayData assayData = validationEnvelope.getEntityToValidate();

        Collection<ValidationMessage<Origin>> validationMessages = executeSubmittableValidation(assayData, enaRunProcessor);

        publishValidationMessage(assayData, validationMessages, validationEnvelope.getValidationResultUUID());
    }
}