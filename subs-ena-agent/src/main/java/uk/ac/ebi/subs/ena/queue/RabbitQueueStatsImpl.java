package uk.ac.ebi.subs.ena.queue;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Properties;

/**
 * Created by neilg on 17/05/2017.
 */

@Component
public class RabbitQueueStatsImpl implements RabbitQueueStats {


    @Override
    public int getQueueSize(String queueName) {
        return 0;
    }
}
