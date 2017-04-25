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
public class Submission extends AbstractSubmittable<Study>{

    @Id
    @GenericGenerator(name = "sraSubmissionGen", strategy = "uk.ac.ebi.subs.ena.id.SubmissionIDGenerator")
    @GeneratedValue(generator = "sraSubmissionGen")
    String id;

    @Column(name = "SUBMISSION_ALIAS")
    String alias;

    @Column(name="STUDY_XML")
    @Type(type="uk.ac.ebi.subs.ena.type.XMLType")
    Document document;

    public Submission(String submissionId, Document document, int statusId) {
        super(submissionId, statusId);
        this.document = document;
    }

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

