package uk.ac.ebi.subs.ena.data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by neilg on 26/04/2017.
 */
@MappedSuperclass
public abstract class AbstractSubmittableSRAInfo<T extends AbstractSubmittableSRAInfo> extends AbstractSRAInfo<T> implements SubmittableSRAInfo {
    @Column(name = "SUBMISSION_ID")
    String submissionId = null;

    @Column(name = "STATUS_ID")
    int statusId;

    public AbstractSubmittableSRAInfo(String submissionId) {
        this.submissionId = submissionId;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public SubmissionStatus getSubmissionStatus() {
        return SubmissionStatus.getSubmissionStatus(statusId);
    }

    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        setStatusId(submissionStatus.getStatusId());
    }

    public AbstractSubmittableSRAInfo() {
        super();
    }
}
