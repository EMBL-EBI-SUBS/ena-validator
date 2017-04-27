package uk.ac.ebi.subs.ena.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import uk.ac.ebi.subs.ena.data.SRAInfo;
import uk.ac.ebi.subs.ena.data.Submittable;
import uk.ac.ebi.subs.ena.data.SubmittableSRAInfo;

import java.util.List;

/**
 * Created by neilg on 26/04/2017.
 */
@NoRepositoryBean
public interface SubmittableSRARepository<T extends SubmittableSRAInfo> extends SRARepository<T>, PagingAndSortingRepository<T, String> {
    List<T> findBySubmissionId(String submissionId);
}
