package uk.ac.ebi.subs.ena.repository;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.subs.ena.data.Submission;
import uk.ac.ebi.subs.ena.data.SubmissionStatus;

/**
 * Created by neilg on 26/04/2017.
 */

public class SubmissionRepositoryTest extends SRAInfoRepositoryTest<Submission,SubmissionRepository> {

    @Override
    public String getId() {
        return "ERA000001";
    }

    @Override
    public String getAlias() {
        return "Katryn Holt";
    }

    @Override
    public Submission createSubmittable(String alias, String submissionAccountId, SubmissionStatus submissionStatus) {
        return null;
    }

    @Autowired
    SubmissionRepository setSubmissionRepository(SubmissionRepository repository) {
        return this.repository = repository;
    }
}
