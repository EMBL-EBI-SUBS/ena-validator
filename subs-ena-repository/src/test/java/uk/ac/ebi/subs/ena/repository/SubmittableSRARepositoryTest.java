package uk.ac.ebi.subs.ena.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uk.ac.ebi.subs.ena.data.SubmittableSRAInfo;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertFalse;

/**
 * Created by neilg on 26/04/2017.
 */

public abstract class SubmittableSRARepositoryTest<E extends SubmittableSRAInfo, T extends SubmittableSRARepository<E>> extends SRAInfoRepositoryTest<E,T> {

    @Value("${ena.submission_account_id}")
    String submissionAccountId;

    @Test
    public void findBySubmissionId() throws Exception {
        String id = getSubmissionId();
        final List<? extends SubmittableSRAInfo> submittableSRAInfoList = repository.findBySubmissionId(id);
        assertFalse(submittableSRAInfoList.isEmpty());
    }

    @Test
    public void findBySubmissionAccountId() throws Exception {
        final List<? extends SubmittableSRAInfo> submittableSRAInfoList = repository.findBySubmissionAccountId("Webin-30");
        logger.info("Returned list of size " + submittableSRAInfoList.size(), " for findBySubmissionAccountId");
        assertFalse(submittableSRAInfoList.isEmpty());
    }

    @Test
    public void findBySubmissionAccountIdAndStatusId() throws Exception {
        final List<? extends SubmittableSRAInfo> submittableSRAInfoList = repository.findBySubmissionAccountIdAndStatusId(submissionAccountId,4);
        logger.info("Returned list of size " + submittableSRAInfoList.size(), " for findBySubmissionAccountIdAndStatusId");
        assertFalse(submittableSRAInfoList.isEmpty());
    }

}
