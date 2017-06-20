package uk.ac.ebi.subs.ena.repository;

import org.junit.Test;
import uk.ac.ebi.subs.ena.data.SubmittableSRAInfo;

import java.util.List;

import static org.junit.Assert.assertFalse;

/**
 * Created by neilg on 26/04/2017.
 */

public abstract class SubmittableSRARepositoryTest<E extends SubmittableSRAInfo, T extends SubmittableSRARepository<E>> extends SRAInfoRepositoryTest<E,T> {

    @Test
    public void findBySubmissionId() throws Exception {
        String id = getSubmissionId();
        final List<? extends SubmittableSRAInfo> submittableSRAInfoList = repository.findBySubmissionId(id);
        assertFalse(submittableSRAInfoList.isEmpty());
    }

}
