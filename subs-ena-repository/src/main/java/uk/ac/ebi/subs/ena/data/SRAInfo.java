package uk.ac.ebi.subs.ena.data;

import org.w3c.dom.Document;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

/**
 * Created by neilg on 02/04/2017.
 */
public interface SRAInfo {
    public String getId();

    public void setId(String id);

    public String getAlias();

    public void setAlias (String alias);

    public Document getDocument();

    public void setDocument(Document document);

    public String getSubmissionAccountId();

    public void setSubmissionAccountId(String submissionAccountId);

    public String getMD5();

    public void setMD5(String md5);

    public Timestamp getFirstCreated();

    public void setFirstCreated(Timestamp firstCreated);

    public void updateMD5 ();


}
