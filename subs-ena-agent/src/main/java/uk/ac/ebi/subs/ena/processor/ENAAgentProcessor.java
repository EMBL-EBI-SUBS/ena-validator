package uk.ac.ebi.subs.ena.processor;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.FullSubmission;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neilg on 12/04/2017.
 */
//@Service
public class ENAAgentProcessor implements AgentProcessor {

    @Override
    public List<ProcessingCertificate> processSubmission(SubmissionEnvelope submissionEnvelope) {
        final FullSubmission submission = submissionEnvelope.getSubmission();
        final List<Study> studies = submission.getStudies();
        return new ArrayList<>();
    }

    Connection connection;
    boolean commitSubmittable;

    public ENAAgentProcessor(Connection connection, boolean commitSubmittable) {
        this.connection = connection;
        this.commitSubmittable = commitSubmittable;
    }

    static boolean isUpdate (Submittable submittable) {
        if (submittable.getAccession() != null && !submittable.getAccession().isEmpty())
            return true;
        else
            return false;
    }
}
