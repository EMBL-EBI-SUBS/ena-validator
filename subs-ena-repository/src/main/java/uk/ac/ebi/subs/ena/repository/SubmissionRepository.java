package uk.ac.ebi.subs.ena.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.subs.ena.data.SRAInfo;
import uk.ac.ebi.subs.ena.data.Submission;

/**
 * Created by neilg on 02/04/2017.
 */
@RepositoryRestResource(collectionResourceRel = "submission", path = "submission")
public interface SubmissionRepository extends SRARepository<Submission> {
}
