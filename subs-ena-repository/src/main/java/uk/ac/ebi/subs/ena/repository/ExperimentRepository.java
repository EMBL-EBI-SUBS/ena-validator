package uk.ac.ebi.subs.ena.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.subs.ena.data.Experiment;

/**
 * Created by neilg on 26/04/2017.
 */

@RepositoryRestResource(collectionResourceRel = "experiment", path = "experiment")
public interface ExperimentRepository extends SubmittableSRARepository<Experiment> {
}

