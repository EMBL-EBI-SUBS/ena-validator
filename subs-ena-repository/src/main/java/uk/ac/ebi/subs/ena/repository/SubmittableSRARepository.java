package uk.ac.ebi.subs.ena.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import uk.ac.ebi.subs.ena.data.SubmittableSRAInfo;

import java.util.List;

/**
 * Created by neilg on 26/04/2017.
 */
@NoRepositoryBean
public interface SubmittableSRARepository<T extends SubmittableSRAInfo> extends SRARepository<T>, PagingAndSortingRepository<T, String> {
    List<T> findBySubmissionId(String submissionId);
    List<T> findBySubmissionAccountIdAndStatusId(String submissionAccountId, int statusId);
    Long countBySubmissionAccountIdAndStatusId(String submissionAccountId, int statusId);
    List<T> findBySubmissionAccountIdAndStatusId(String submissionAccountId, int statusId, Pageable pageable);
}
