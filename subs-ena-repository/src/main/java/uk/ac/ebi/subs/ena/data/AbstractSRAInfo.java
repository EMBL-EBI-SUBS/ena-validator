package uk.ac.ebi.subs.ena.data;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by neilg on 01/04/2017.
 */
@MappedSuperclass
public abstract class AbstractSRAInfo<T extends AbstractSRAInfo> implements SRAInfo {

    @Column(name = "SUBMISSION_ACCOUNT_ID")
    String submissionAccountId;

    @Column(name = "VERSION")
    String version;

    @Column(name = "EGA_ID")
    String egaId;

    @Column(name = "FIRST_CREATED")
    Timestamp firstCreated;

    public AbstractSRAInfo() {
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
}
