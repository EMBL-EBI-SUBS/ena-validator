package uk.ac.ebi.subs.ena.queue;

import org.springframework.stereotype.Component;

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
