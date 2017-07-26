package uk.ac.ebi.subs.ena.processor;

import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.List;

/**
 * Created by neilg on 12/04/2017.
 */
public interface AgentProcessor<T extends Submittable> {
    ProcessingCertificate process(T submittable);
}
