package uk.ac.ebi.subs.ena.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import uk.ac.ebi.subs.ena.data.AbstractSRAInfo;
import uk.ac.ebi.subs.ena.data.SRAInfo;

import java.util.List;

/**
 * Created by neilg on 25/04/2017.
 */
@NoRepositoryBean
public interface SRARepository<T extends SRAInfo> extends CrudRepository<T, String> {
    T findByAliasAndSubmissionAccountId(String alias, String submissionAccountId);

    @Override
    <S extends T> S save(S entity);

    @Override
    <S extends T> Iterable<S> save(Iterable<S> entities);

    @Override
    T findOne(String s);

    @Override
    boolean exists(String s);

    @Override
    Iterable<T> findAll();

    @Override
    Iterable<T> findAll(Iterable<String> strings);

    @Override
    long count();

    @Override
    void delete(String s);

    @Override
    void delete(T entity);

    @Override
    void delete(Iterable<? extends T> entities);

    @Override
    void deleteAll();
}


