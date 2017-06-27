package uk.ac.ebi.subs.ena.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by karoly on 13/06/2017.
 */
@Configuration
public class RabbitStartupConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        rabbitAdmin.initialize();
    }
}
