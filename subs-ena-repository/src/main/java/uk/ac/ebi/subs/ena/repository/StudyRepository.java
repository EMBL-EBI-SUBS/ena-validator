package uk.ac.ebi.subs.ena.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.ebi.subs.ena.data.Study;

import java.util.List;

/**
 * Created by neilg on 02/04/2017.
 */
public interface StudyRepository extends CrudRepository<Study,String> {
    List<Study> findBySubmissionAccountId(String submissionAccountId);
    List<Study> findBySubmissionId(String submissionId);
}
