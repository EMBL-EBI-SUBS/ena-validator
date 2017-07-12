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
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationAuthor;
import uk.ac.ebi.subs.validator.data.ValidationStatus;
import uk.ac.ebi.subs.validator.messaging.Exchanges;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by karoly on 14/06/2017.
 */
public interface EnaAgentValidator {

    Logger logger = LoggerFactory.getLogger(EnaAgentValidator.class);

    void setRabbitMessagingTemplate(RabbitMessagingTemplate rabbitMessagingTemplate);
    RabbitMessagingTemplate getRabbitMessagingTemplate();

    default List<SingleValidationResult> executeSubmittableValidation(Submittable submittable,
                                                                       ENAAgentProcessor eNAAgentProcessor) {
        List<SingleValidationResult> singleValidationResultCollection = new ArrayList<>();

        try {
            ENASubmittable eNASubmittable= eNAAgentProcessor.convertFromSubmittableToENASubmittable(submittable,singleValidationResultCollection);
            singleValidationResultCollection.addAll(eNAAgentProcessor.validateEntity(eNASubmittable));
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("An exception occured: {}", e.getMessage());
            SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Ena,submittable.getId());
            singleValidationResult.setMessage(e.getMessage());
            singleValidationResult.setValidationStatus(ValidationStatus.Error);
            singleValidationResultCollection.add(singleValidationResult);
        }


        return singleValidationResultCollection;
    }

    default void publishValidationMessage(Submittable submittable, List<SingleValidationResult> singleValidationResultCollection,
                                          String validationResultUuid, int validationResultVersion) {

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = new SingleValidationResultsEnvelope(
                singleValidationResultCollection,validationResultVersion,validationResultUuid,ValidationAuthor.Ena
        );

        if (hasValidationError(singleValidationResultCollection)) {
            getRabbitMessagingTemplate().convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_VALIDATION_SUCCESS,singleValidationResultsEnvelope);

            logger.info("Validation successful for {} entity with id: {}", submittable.getClass().getSimpleName(), submittable.getId());
        } else {
            getRabbitMessagingTemplate().convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_VALIDATION_ERROR,singleValidationResultsEnvelope);

            logger.info("Validation erred for {} entity with id: {}", submittable.getClass().getSimpleName(), submittable.getId());
        }
    }

    default boolean hasValidationError(List<SingleValidationResult> validationResults) {
        SingleValidationResult errorValidationResult = validationResults.stream().filter(
                validationResult -> validationResult.getValidationStatus() == ValidationStatus.Error)
                .findAny()
                .orElse(null);

        return errorValidationResult != null;
    }

}
