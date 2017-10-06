package uk.ac.ebi.subs.export;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "ena.export")
@Component
class ExportServiceProperties {

    String [] services = {"StudyExportService","SampleExportService","ExperimentExportService"};

    @NotEmpty
    String path;

    @NotEmpty
    String submissionAccountId;

    boolean bySubmission = true;

    boolean completeSubmissionFormat = true;

    public String[] getServices() {
        return services;
    }

    public void setServices(String[] services) {
        this.services = services;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSubmissionAccountId() {
        return submissionAccountId;
    }

    public void setSubmissionAccountId(String submissionAccountId) {
        this.submissionAccountId = submissionAccountId;
    }

    public boolean isBySubmission() {
        return bySubmission;
    }

    public void setBySubmission(boolean bySubmission) {
        this.bySubmission = bySubmission;
    }

    public boolean isCompleteSubmissionFormat() {
        return completeSubmissionFormat;
    }

    public void setCompleteSubmissionFormat(boolean completeSubmissionFormat) {
        this.completeSubmissionFormat = completeSubmissionFormat;
    }
}
