package uk.ac.ebi.subs.ena.data;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.w3c.dom.Document;

import javax.persistence.*;

/**
 * Created by neilg on 02/04/2017.
 */

@Entity
@Table(name="SUBMISSION")
public class Submission extends AbstractSRAInfo<Submission> implements SRAInfo {

    @Id
    @GenericGenerator(name = "sraSubmissionGen", strategy = "uk.ac.ebi.subs.ena.id.SubmissionIDGenerator")
    @GeneratedValue(generator = "sraSubmissionGen")
    @Column(name = "SUBMISSION_ID")
    String id;

    @Column(name = "SUBMISSION_ALIAS")
    String alias;

    @Column(name="SUBMISSION_XML")
    @Type(type="uk.ac.ebi.subs.ena.type.XMLType")
    Document document;

    @Column(name = "SUBMISSION_ID", insertable = false, updatable = false)
    String submissionId;

    public Submission () {
        super();
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public void setDocument(Document document) {
        this.document = document;
    }

    @Override
    public String getId() {
        return submissionId;
    }

    @Override
    public void setId(String id) {
        this.submissionId = submissionId;
    }

    @Override
    public String getAlias() {
        return alias;
    }

}

