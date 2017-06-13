package uk.ac.ebi.subs.ena;

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
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.ena.processor.ENAProcessorContainerService;
import uk.ac.ebi.subs.ena.processor.ENASampleProcessor;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationStatus;
import uk.ac.ebi.subs.validator.messaging.Exchanges;
import uk.ac.ebi.subs.validator.messaging.Queues;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class responsible to do the ENA related validations.
 */
@Service
public class EnaAgentValidator {

    private static final Logger logger = LoggerFactory.getLogger(EnaAgentValidator.class);

    RabbitMessagingTemplate rabbitMessagingTemplate;

    ENAProcessorContainerService enaProcessorContainerService;

    @Autowired
    ENASampleProcessor enaSampleProcessor;

    public static final String SUCCESS_MESSAGE = "Passed ENA Sample validation with no errors";
    public static final String NULL_SAMPLE_ERROR_MESSAGE = "Sample is null";

    @Autowired
    public EnaAgentValidator(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter, ENAProcessorContainerService enaProcessorContainerService) {
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
    @RabbitListener(queues = Queues.ENA_SAMPLE_VALIDATION)
    public void validateSample(ValidationMessageEnvelope<Sample> validationEnvelope) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        final Sample sample = validationEnvelope.getEntityToValidate();

        Collection<ValidationMessage<Origin>> validationMessages = executeSubmittableValidation(sample);

        publishValidationMessage(sample, validationMessages);
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

        Collection<ValidationMessage<Origin>> validationMessages = executeSubmittableValidation(study);

        publishValidationMessage(study, validationMessages);
    }

    Collection<ValidationMessage<Origin>> executeSubmittableValidation(Submittable submittable) {
        Collection<ValidationMessage<Origin>> validationMessages = new ArrayList<>();

        if (submittable == null) {
            enaSampleProcessor.addNullSubmittableValidationMessage(validationMessages,
                    enaSampleProcessor.getSubmittableObjectTypeAsAString());
        } else {
            try {
                enaSampleProcessor.convertFromSubmittableToENASubmittable(submittable);
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("An exception occured: {}", e.getMessage());
                validationMessages.add(ValidationMessage.error("ERAM.1.0.3", e.getMessage()));
            }
        }

        return validationMessages;
    }

    private void publishValidationMessage(Submittable submittable, Collection<ValidationMessage<Origin>> validationMessages) {
        String validationMessage = assembleErrorMessage(validationMessages);

        if (validationMessages.isEmpty()) {
            rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_VALIDATION_SUCCESS,
                    buildSingleValidationResult(submittable, ValidationStatus.Pass, validationMessage));

            logger.info("Validation successful for {} entity with id: {}", submittable.getClass().getSimpleName(), submittable.getId());
        } else {
            rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_VALIDATION_ERROR,
                    buildSingleValidationResult(submittable, ValidationStatus.Error, validationMessage));

            logger.info("Validation erred for {} entity with id: {}", submittable.getClass().getSimpleName(), submittable.getId());
        }
    }

    /**
     * Assemble a collection of error messages to a String. If the collection is empty,
     * then it will return a success message.
     * @param validationMessages Collection of validation messages
     * @return A list of validation messages converted into a String or a success message, if there were no errors.
     */
    String assembleErrorMessage(Collection<ValidationMessage<Origin>> validationMessages) {
        String assembledValidationMessage = validationMessages.stream()
                .map(ValidationMessage::getMessage)
                .collect(Collectors.joining(", "));

        if (assembledValidationMessage == null || assembledValidationMessage.equals("")) {
            assembledValidationMessage = SUCCESS_MESSAGE;
        }

        return assembledValidationMessage;
    }

    private SingleValidationResult buildSingleValidationResult(Submittable submittable, ValidationStatus status, String validationMessages) {
        SingleValidationResult singleValidationResult = new SingleValidationResult(Archive.Ena, submittable.getId());
        singleValidationResult.setUuid(UUID.randomUUID().toString());
        singleValidationResult.setValidationStatus(status);

        singleValidationResult.setMessage(validationMessages);

        return singleValidationResult;
    }
}