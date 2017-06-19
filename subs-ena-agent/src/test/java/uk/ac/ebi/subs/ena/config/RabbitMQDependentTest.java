package uk.ac.ebi.subs.ena.config;

/**
 * This is just a marker interface for tests depends on a running RabbitMQ instance.
 * We can use this marker interface for integration tests that we don't want to execute on CI server.
 *
 * Created by karoly on 19/06/2017.
 */
public interface RabbitMQDependentTest {
}
