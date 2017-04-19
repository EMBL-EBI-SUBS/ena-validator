package uk.ac.ebi.subs.ena.data;

import org.w3c.dom.Document;

import javax.persistence.Column;

/**
 * Created by neilg on 01/04/2017.
 */
public abstract class AbstractSubmittable<T extends AbstractSubmittable> implements Submittable{
    @Column(name = "SUBMISSION_ID")
    protected String submissionId = null;
    @Column(name="STATUS_ID")
    int statusId;
    @Column(name="SUBMISSION_ACCOUNT_ID")
    String submissionAccountId;

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
}
