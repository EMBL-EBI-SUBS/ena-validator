package uk.ac.ebi.subs.ena.data;

import org.w3c.dom.Document;

/**
 * Created by neilg on 02/04/2017.
 */
public interface Submittable {
    public String getId();

    public void setId(String id);

    public String getAlias();

    public Document getDocument();

    public void setDocument(Document document);

    public String getSubmissionId();

    public void setSubmissionId(String submissionId);

    public int getStatusId();

    public void setStatusId(int statusId);

    public String getSubmissionAccountId();

    public void setSubmissionAccountId(String submissionAccountId);


}
