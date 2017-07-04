package uk.ac.ebi.subs.ena.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.subs.validator.messaging.Queues;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;
import uk.ac.ebi.subs.validator.messaging.ValidationExchangeConfig;

/**
 * This class holds the messaging configuration for the ENA validator.
 * It defines the queues and their bindings.
 *
 * Created by karoly on 04/07/2017.
 */
@Configuration
@ComponentScan(basePackageClasses = ValidationExchangeConfig.class)
public class EnaValidatorQueueConfiguration {

    /**
     * Instantiate a {@link Queue} for validate studies related to ENA.
     *
     * @return an instance of a {@link Queue} for validate studies related to ENA.
     */
    @Bean
    Queue enaStudyQueue() {
        return new Queue(Queues.ENA_STUDY_VALIDATION, true);
    }

    /**
     * Create a {@link Binding} between the validation exchange and ENA study validation queue
     * using the routing key of created studies related to ENA.
     *
     * @param enaStudyQueue {@link Queue} for validating ENA related studies
     * @param validationExchange {@link TopicExchange} for validation
     * @return a {@link Binding} between the validation exchange and ENA study validation queue
     * using the routing key of created studies related to ENA.
     */
    @Bean
    Binding validationForCreatedENAStudyBinding(Queue enaStudyQueue, TopicExchange validationExchange) {
        return BindingBuilder.bind(enaStudyQueue).to(validationExchange)
                .with(RoutingKeys.EVENT_ENA_STUDY_VALIDATION);
    }

    /**
     * Instantiate a {@link Queue} for validate samples related to ENA.
     *
     * @return an instance of a {@link Queue} for validate samples related to ENA.
     */
    @Bean
    Queue enaSampleQueue() {
        return new Queue(Queues.ENA_SAMPLE_VALIDATION, true);
    }

    /**
     * Create a {@link Binding} between the validation exchange and ENA sample validation queue
     * using the routing key of created samples related to ENA.
     *
     * @param enaSampleQueue {@link Queue} for validating ENA related samples
     * @param validationExchange {@link TopicExchange} for validation
     * @return a {@link Binding} between the validation exchange and ENA sample validation queue
     * using the routing key of created samples related to ENA.
     */
    @Bean
    Binding validationForCreatedENASampleBinding(Queue enaSampleQueue, TopicExchange validationExchange) {
        return BindingBuilder.bind(enaSampleQueue).to(validationExchange)
                .with(RoutingKeys.EVENT_ENA_SAMPLE_VALIDATION);
    }

    /**
     * Instantiate a {@link Queue} for validate assays related to ENA.
     *
     * @return an instance of a {@link Queue} for validate assays related to ENA.
     */
    @Bean
    Queue enaAssayQueue() {
        return new Queue(Queues.ENA_ASSAY_VALIDATION, true);
    }

    /**
     * Create a {@link Binding} between the validation exchange and ENA assay validation queue
     * using the routing key of created assays related to ENA.
     *
     * @param enaAssayQueue {@link Queue} for validating ENA related assays
     * @param validationExchange {@link TopicExchange} for validation
     * @return a {@link Binding} between the validation exchange and ENA assay validation queue
     * using the routing key of created assays related to ENA.
     */
    @Bean
    Binding validationForCreatedENAAssayBinding(Queue enaAssayQueue, TopicExchange validationExchange) {
        return BindingBuilder.bind(enaAssayQueue).to(validationExchange)
                .with(RoutingKeys.EVENT_ENA_ASSAY_VALIDATION);
    }

    /**
     * Instantiate a {@link Queue} for validate assay data related to ENA.
     *
     * @return an instance of a {@link Queue} for validate assay data related to ENA.
     */
    @Bean
    Queue enaAssayDataQueue() {
        return new Queue(Queues.ENA_ASSAYDATA_VALIDATION, true);
    }

    /**
     * Create a {@link Binding} between the validation exchange and ENA assay data validation queue
     * using the routing key of created assay data related to ENA.
     *
     * @param enaAssayDataQueue {@link Queue} for validating ENA related assay data
     * @param validationExchange {@link TopicExchange} for validation
     * @return a {@link Binding} between the validation exchange and ENA assay data validation queue
     * using the routing key of created assay data related to ENA.
     */
    @Bean
    Binding validationForCreatedENAAssayDataBinding(Queue enaAssayDataQueue, TopicExchange validationExchange) {
        return BindingBuilder.bind(enaAssayDataQueue).to(validationExchange)
                .with(RoutingKeys.EVENT_ENA_ASSAYDATA_VALIDATION);
    }
}
