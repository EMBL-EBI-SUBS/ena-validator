package uk.ac.ebi.subs.ena.data;

import org.hibernate.annotations.GenericGenerator;
import org.w3c.dom.Document;

import javax.persistence.*;

/**
 * Created by neilg on 02/04/2017.
 */

@Entity
@Table(name="STUDY")
public class Study extends AbstractSubmittable<Study>{
    @Id
    @GenericGenerator(name = "sraStudyGen", strategy = "uk.ac.ebi.subs.ena.id.StudyIDGenerator")
    @GeneratedValue(generator = "sequence_emp_id")
    @Column(name = "STUDY_ID")
    String id;
    @Column(name="STUDY_XML")
    Document document;


    public Study(String id, Document document, String submissionId, int statusId) {
        super(submissionId, statusId);
        this.id = id;
        this.document = document;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public void setDocument(Document document) {
        this.document = document;
    }
}
