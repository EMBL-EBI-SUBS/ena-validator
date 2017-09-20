package uk.ac.ebi.subs.export;

import java.nio.file.Path;

public interface ExportService {
    void export (Path path, String submissionAccountId);
    String getName ();
}
