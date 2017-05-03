package uk.ac.ebi.subs.ena.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.subs.ena.data.Study;

/**
 * Created by neilg on 02/04/2017.
 */
@RepositoryRestResource(collectionResourceRel = "study", path = "study")
public interface StudyRepository extends SubmittableSRARepository<Study> {

}
