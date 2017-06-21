package uk.ac.ebi.subs.ena.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.ValidationMessage;
import uk.ac.ebi.subs.data.submittable.ENASubmittable;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.ena.processor.ENAAgentProcessor;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationAuthor;
import uk.ac.ebi.subs.validator.data.ValidationStatus;
import uk.ac.ebi.subs.validator.messaging.Exchanges;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by karoly on 14/06/2017.
 */
public interface EnaAgentValidator {

    Logger logger = LoggerFactory.getLogger(EnaAgentValidator.class);

    void setRabbitMessagingTemplate(RabbitMessagingTemplate rabbitMessagingTemplate);
    RabbitMessagingTemplate getRabbitMessagingTemplate();

    String SUCCESS_MESSAGE = "Passed ENA %s validation with no errors";
    String NULL_SAMPLE_ERROR_MESSAGE = "%s is null";

    default Collection<ValidationMessage<Origin>> executeSubmittableValidation(Submittable submittable,
                                                                       ENAAgentProcessor eNAAgentProcessor) {
        Collection<ValidationMessage<Origin>> validationMessages = new ArrayList<>();

        if (submittable == null) {
            eNAAgentProcessor.addNullSubmittableValidationMessage(validationMessages,
                    eNAAgentProcessor.getSubmittableObjectTypeAsAString());
        } else {
            try {
                ENASubmittable eNASubmittable= eNAAgentProcessor.convertFromSubmittableToENASubmittable(submittable);
                validationMessages = eNAAgentProcessor.validateEntity(eNASubmittable);
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("An exception occured: {}", e.getMessage());
                validationMessages.add(ValidationMessage.error("ERAM.1.0.3", e.getMessage()));
            }
        }

        return validationMessages;
    }

    default void publishValidationMessage(Submittable submittable, Collection<ValidationMessage<Origin>> validationMessages,
                                          String validationResultUuid) {
        String validationMessage = assembleValidationMessage(validationMessages, submittable.getClass().getSimpleName());

        if (validationMessages.isEmpty()) {
            getRabbitMessagingTemplate().convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_VALIDATION_SUCCESS,
                    buildSingleValidationResult(submittable, ValidationStatus.Pass, validationMessage, validationResultUuid));

            logger.info("Validation successful for {} entity with id: {}", submittable.getClass().getSimpleName(), submittable.getId());
        } else {
            getRabbitMessagingTemplate().convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_VALIDATION_ERROR,
                    buildSingleValidationResult(submittable, ValidationStatus.Error, validationMessage, validationResultUuid));

            logger.info("Validation erred for {} entity with id: {}", submittable.getClass().getSimpleName(), submittable.getId());
        }
    }

    default SingleValidationResult buildSingleValidationResult(Submittable submittable, ValidationStatus status,
                                                               String validationMessages, String validationResultUuid) {
        SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Ena, submittable.getId());
        singleValidationResult.setUuid(UUID.randomUUID().toString());
        singleValidationResult.setEntityUuid(submittable.getId());
        singleValidationResult.setValidationStatus(status);

        singleValidationResult.setMessage(validationMessages);
        singleValidationResult.setValidationResultUUID(validationResultUuid);

        return singleValidationResult;
    }

    /**
     * Assemble a collection of validation messages to a String. If the collection is empty,
     * then it will return a success message.
     * @param validationMessages Collection of validation messages
     * @return A list of validation messages converted into a String or a success message, if there were no validation errors.
     */
    default String assembleValidationMessage(Collection<ValidationMessage<Origin>> validationMessages, String submittableType) {
        String assembledValidationMessage = validationMessages.stream()
                .map(ValidationMessage::getMessage)
                .collect(Collectors.joining(", "));

        if (assembledValidationMessage == null || assembledValidationMessage.equals("")) {
            assembledValidationMessage = String.format(SUCCESS_MESSAGE, submittableType);
        }

        return assembledValidationMessage;
    }

}
