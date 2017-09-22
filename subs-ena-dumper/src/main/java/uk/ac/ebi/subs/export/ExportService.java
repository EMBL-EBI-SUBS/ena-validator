package uk.ac.ebi.subs.export;

import uk.ac.ebi.subs.stresstest.ClientCompleteSubmission;

import java.nio.file.Path;

public interface ExportService {
    void export (Path path, String submissionAccountId);
    void exportBySubmissionId (Path path, String submissionId);
    void export(ClientCompleteSubmission clientCompleteSubmission, String submissionId);
    String getName ();
}
