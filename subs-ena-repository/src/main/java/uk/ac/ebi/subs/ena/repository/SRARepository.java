package uk.ac.ebi.subs.ena.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import uk.ac.ebi.subs.ena.data.AbstractSRAInfo;
import uk.ac.ebi.subs.ena.data.SRAInfo;

import java.util.List;

/**
 * Created by neilg on 25/04/2017.
 */
@NoRepositoryBean
public interface SRARepository<T extends SRAInfo> extends PagingAndSortingRepository<T, String> {
    T findByAliasAndSubmissionAccountId(String alias, String submissionAccountId);

}


