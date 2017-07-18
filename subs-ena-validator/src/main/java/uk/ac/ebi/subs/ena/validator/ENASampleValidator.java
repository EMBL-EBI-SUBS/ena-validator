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
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.ena.processor.ENAProcessorContainerService;
import uk.ac.ebi.subs.ena.processor.ENASampleProcessor;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.messaging.Queues;

import java.util.List;

/**
 * This class responsible to do the ENA related validations.
 */
@Service
public class ENASampleValidator implements ENAValidator {

    private static final Logger logger = LoggerFactory.getLogger(ENASampleValidator.class);

    @Autowired
    ENASampleProcessor enaSampleProcessor;

    public ENASampleProcessor getEnaSampleProcessor() {
        return enaSampleProcessor;
    }

    public void setEnaSampleProcessor(ENASampleProcessor eNASampleProcessor) {
        this.enaSampleProcessor = eNASampleProcessor;
    }

    RabbitMessagingTemplate rabbitMessagingTemplate;

    ENAProcessorContainerService enaProcessorContainerService;

    @Override
    public void setRabbitMessagingTemplate(RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    @Override
    public RabbitMessagingTemplate getRabbitMessagingTemplate() {
        return rabbitMessagingTemplate;
    }

    public ENASampleValidator(RabbitMessagingTemplate rabbitMessagingTemplate,
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
    @RabbitListener(queues = Queues.ENA_SAMPLE_VALIDATION)
    public void validateSample(ValidationMessageEnvelope<Sample> validationEnvelope) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        final Sample sample = validationEnvelope.getEntityToValidate();
        final List<SingleValidationResult> singleValidationResultCollection = executeSubmittableValidation(sample, enaSampleProcessor);
        checkForEmptySingleValidationResult(singleValidationResultCollection,sample);
        publishValidationMessage(sample,singleValidationResultCollection,validationEnvelope.getValidationResultUUID(),validationEnvelope.getValidationResultVersion());
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}