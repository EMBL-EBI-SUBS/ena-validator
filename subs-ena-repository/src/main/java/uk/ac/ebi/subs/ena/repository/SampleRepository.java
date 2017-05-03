package uk.ac.ebi.subs.ena.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.subs.ena.data.Sample;

/**
 * Created by neilg on 25/04/2017.
 */
@RepositoryRestResource(collectionResourceRel = "sample", path = "sample")
public interface SampleRepository extends SubmittableSRARepository<Sample> {
}
