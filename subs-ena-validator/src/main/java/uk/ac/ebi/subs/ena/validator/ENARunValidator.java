package uk.ac.ebi.subs.ena.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.ena.processor.ENAProcessorContainerService;
import uk.ac.ebi.subs.ena.processor.ENARunProcessor;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.util.List;

import static uk.ac.ebi.subs.ena.config.EnaValidatorQueues.ENA_ASSAYDATA_VALIDATION;

/**
 * This class responsible to do the ENA related validations.
 */
@Service
public class ENARunValidator implements ENAValidator {

    private static final Logger logger = LoggerFactory.getLogger(ENARunValidator.class);

    @Autowired
    ENARunProcessor enaRunProcessor;

    public ENARunProcessor getEnaRunProcessor() {
        return enaRunProcessor;
    }

    public void setEnaRunProcessor(ENARunProcessor eNARunProcessor) {
        this.enaRunProcessor = eNARunProcessor;
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

    public ENARunValidator(RabbitMessagingTemplate rabbitMessagingTemplate,
                           ENAProcessorContainerService enaProcessorContainerService) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.enaProcessorContainerService = enaProcessorContainerService;
    }


    /**
     * Do a validation for the {@link AssayData} submitted in the {@link AssayDataValidationMessageEnvelope}.
     * It produces a message according to the validation outcome.
     *
     * @param validationEnvelope {@link AssayDataValidationMessageEnvelope} that contains the assay data to validate
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @RabbitListener(queues = ENA_ASSAYDATA_VALIDATION)
    public void validateAssayData(AssayDataValidationMessageEnvelope validationEnvelope) {
        final AssayData assayData = validationEnvelope.getEntityToValidate();
        final List<SingleValidationResult> singleValidationResultCollection = executeSubmittableValidation(assayData, enaRunProcessor);
        checkForEmptySingleValidationResult(singleValidationResultCollection,assayData);
        publishValidationMessage(assayData,singleValidationResultCollection,validationEnvelope.getValidationResultUUID(),validationEnvelope.getValidationResultVersion());
    }
}