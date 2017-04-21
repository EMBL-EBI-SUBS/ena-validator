package uk.ac.ebi.subs.ena.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.ebi.subs.ena.data.Submission;

import java.util.List;

/**
 * Created by neilg on 02/04/2017.
 */
public interface SubmissionRepository extends CrudRepository<Submission,String> {
    List<Submission> findBySubmissionAccountId(String submissionAccountId);
}
