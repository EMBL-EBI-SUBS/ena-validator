package uk.ac.ebi.subs.ena.data;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

/**
 * Created by neilg on 26/04/2017.
 */
public interface Submittable {
    public String getSubmissionId();
    public void setSubmissionId(String submissionId);
    public int getStatusId();
    public void setStatusId(int statusId);
    public SubmissionStatus getSubmissionStatus();
    public void setSubmissionStatus(SubmissionStatus submissionStatus);
}
