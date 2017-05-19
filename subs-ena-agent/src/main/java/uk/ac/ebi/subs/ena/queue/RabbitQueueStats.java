package uk.ac.ebi.subs.ena.queue;

/**
 * Created by neilg on 17/05/2017.
 */
public interface RabbitQueueStats {
    public int getQueueSize (String queueName);
}
