package uk.ac.ebi.subs.ena.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.subs.messaging.ExchangeConfig;
import uk.ac.ebi.subs.messaging.Queues;

import static uk.ac.ebi.subs.ena.config.EnaValidatorQueues.*;
import static uk.ac.ebi.subs.ena.config.EnaValidatorRoutingKeys.*;

/**
 * This class holds the messaging configuration for the ENA validator.
 * It defines the queues and their bindings.
 *
 * Created by karoly on 04/07/2017.
 */
@Configuration
@ComponentScan(basePackageClasses = ExchangeConfig.class)
public class EnaValidatorQueueConfiguration {

    /**
     * Instantiate a {@link Queue} for validate studies related to ENA.
     *
     * @return an instance of a {@link Queue} for validate studies related to ENA.
     */
    @Bean
    Queue enaStudyQueue() {
        return Queues.buildQueueWithDlx(ENA_STUDY_VALIDATION);
    }

    /**
     * Create a {@link Binding} between the validation exchange and ENA study validation queue
     * using the routing key of created studies related to ENA.
     *
     * @param enaStudyQueue {@link Queue} for validating ENA related studies
     * @param submissionExchange {@link TopicExchange} for validation
     * @return a {@link Binding} between the validation exchange and ENA study validation queue
     * using the routing key of created studies related to ENA.
     */
    @Bean
    Binding validationForCreatedENAStudyBinding(Queue enaStudyQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(enaStudyQueue).to(submissionExchange)
                .with(EVENT_ENA_STUDY_VALIDATION);
    }

    /**
     * Instantiate a {@link Queue} for validate samples related to ENA.
     *
     * @return an instance of a {@link Queue} for validate samples related to ENA.
     */
    @Bean
    Queue enaSampleQueue() {
        return Queues.buildQueueWithDlx(ENA_SAMPLE_VALIDATION);
    }

    /**
     * Create a {@link Binding} between the validation exchange and ENA sample validation queue
     * using the routing key of created samples related to ENA.
     *
     * @param enaSampleQueue {@link Queue} for validating ENA related samples
     * @param submissionExchange {@link TopicExchange} for validation
     * @return a {@link Binding} between the validation exchange and ENA sample validation queue
     * using the routing key of created samples related to ENA.
     */
    @Bean
    Binding validationForCreatedENASampleBinding(Queue enaSampleQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(enaSampleQueue).to(submissionExchange)
                .with(EVENT_ENA_SAMPLE_VALIDATION);
    }

    /**
     * Instantiate a {@link Queue} for validate assays related to ENA.
     *
     * @return an instance of a {@link Queue} for validate assays related to ENA.
     */
    @Bean
    Queue enaAssayQueue() {
        return Queues.buildQueueWithDlx(ENA_ASSAY_VALIDATION);
    }

    /**
     * Create a {@link Binding} between the validation exchange and ENA assay validation queue
     * using the routing key of created assays related to ENA.
     *
     * @param enaAssayQueue {@link Queue} for validating ENA related assays
     * @param submissionExchange {@link TopicExchange} for validation
     * @return a {@link Binding} between the validation exchange and ENA assay validation queue
     * using the routing key of created assays related to ENA.
     */
    @Bean
    Binding validationForCreatedENAAssayBinding(Queue enaAssayQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(enaAssayQueue).to(submissionExchange)
                .with(EVENT_ENA_ASSAY_VALIDATION);
    }

    /**
     * Instantiate a {@link Queue} for validate assay data related to ENA.
     *
     * @return an instance of a {@link Queue} for validate assay data related to ENA.
     */
    @Bean
    Queue enaAssayDataQueue() {
        return Queues.buildQueueWithDlx(ENA_ASSAYDATA_VALIDATION);
    }

    /**
     * Create a {@link Binding} between the validation exchange and ENA assay data validation queue
     * using the routing key of created assay data related to ENA.
     *
     * @param enaAssayDataQueue {@link Queue} for validating ENA related assay data
     * @param submissionExchange {@link TopicExchange} for validation
     * @return a {@link Binding} between the validation exchange and ENA assay data validation queue
     * using the routing key of created assay data related to ENA.
     */
    @Bean
    Binding validationForCreatedENAAssayDataBinding(Queue enaAssayDataQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(enaAssayDataQueue).to(submissionExchange)
                .with(EVENT_ENA_ASSAYDATA_VALIDATION);
    }
}
