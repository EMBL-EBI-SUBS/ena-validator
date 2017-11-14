package uk.ac.ebi.subs.ena.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.ena.processor.ENAExperimentProcessor;
import uk.ac.ebi.subs.ena.processor.ENAProcessorContainerService;
import uk.ac.ebi.subs.validator.data.AssayValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.util.List;

import static uk.ac.ebi.subs.ena.config.EnaValidatorQueues.ENA_ASSAY_VALIDATION;

/**
 * This class responsible to do the ENA related validations.
 */
@Service
public class ENAExperimentValidator implements ENAValidator {

    private static final Logger logger = LoggerFactory.getLogger(ENAExperimentValidator.class);

    @Autowired
    ENAExperimentProcessor experimentProcessor;

    public ENAExperimentProcessor getEnaExperimentProcessor() {
        return experimentProcessor;
    }

    public void setExperimentProcessor(ENAExperimentProcessor eNAExperimentProcessor) {
        this.experimentProcessor = eNAExperimentProcessor;
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

    public ENAExperimentValidator(RabbitMessagingTemplate rabbitMessagingTemplate,
                                  ENAProcessorContainerService enaProcessorContainerService) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.enaProcessorContainerService = enaProcessorContainerService;
    }


    /**
     * Do a validation for the {@link Assay} submitted in the {@link AssayValidationMessageEnvelope}.
     * It produces a message according to the validation outcome.
     *
     * @param validationEnvelope {@link AssayValidationMessageEnvelope} that contains the assay to validate
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @RabbitListener(queues = ENA_ASSAY_VALIDATION)
    public void validateAssay(AssayValidationMessageEnvelope validationEnvelope) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        final Assay assay = validationEnvelope.getEntityToValidate();
        final List<SingleValidationResult> singleValidationResultCollection = executeSubmittableValidation(assay, experimentProcessor);
        checkForEmptySingleValidationResult(singleValidationResultCollection,assay);
        publishValidationMessage(assay,singleValidationResultCollection,validationEnvelope.getValidationResultUUID(),validationEnvelope.getValidationResultVersion());
    }
}