package uk.ac.ebi.subs.ena.data;

import org.w3c.dom.Document;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by neilg on 01/04/2017.
 */
@MappedSuperclass
public abstract class AbstractSubmittable<T extends AbstractSubmittable> implements Submittable {
    @Column(name = "SUBMISSION_ID")
    String submissionId = null;
    @Column(name = "STATUS_ID")
    int statusId;

    @Column(name = "SUBMISSION_ACCOUNT_ID")
    String submissionAccountId;

    @Column(name = "VERSION")
    String version;

    @Column(name = "EGA_ID")
    String egaId;

    @Column(name = "FIRST_CREATED")
    Timestamp firstCreated;

    public AbstractSubmittable() {
    }

    public AbstractSubmittable(String submissionId, int statusId) {
        this.submissionId = submissionId;
        this.statusId = statusId;
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

    @Override
    public String getSubmissionAccountId() {
        return submissionAccountId;
    }

    @Override
    public void setSubmissionAccountId(String submissionAccountId) {
        this.submissionAccountId = submissionAccountId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEgaId() {
        return egaId;
    }

    public void setEgaId(String egaId) {
        this.egaId = egaId;
    }

    public Timestamp getFirstCreated() {
        return firstCreated;
    }

    public void setFirstCreated(Timestamp firstCreated) {
        this.firstCreated = firstCreated;
    }

    public SubmissionStatus getSubmissionStatus() {
        return SubmissionStatus.getSubmissionStatus(statusId);
    }

    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        setStatusId(submissionStatus.getStatusId());
    }
}
