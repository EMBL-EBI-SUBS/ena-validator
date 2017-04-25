package uk.ac.ebi.subs.ena.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import uk.ac.ebi.subs.ena.data.AbstractSubmittable;

import java.util.List;

/**
 * Created by neilg on 25/04/2017.
 */
@NoRepositoryBean
public interface SubmittableRepository<T extends AbstractSubmittable<T>> extends PagingAndSortingRepository<T, String> {
    List<T> findBySubmissionId(String submissionId);
    T findByAliasAndSubmissionAccountId(String alias, String submissionAccountId);

}


