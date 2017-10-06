package uk.ac.ebi.subs.ena.data;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.w3c.dom.Document;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by neilg on 02/04/2017.
 */

@Entity
@Table(name="STUDY")
public class Study extends AbstractSubmittableSRAInfo<Study> {

    @Id
    @GenericGenerator(name = "sraStudyGen", strategy = "uk.ac.ebi.subs.ena.id.StudyIDGenerator")
    @GeneratedValue(generator = "sraStudyGen")
    @Column(name = "STUDY_ID")
    String id;

    @Column(name = "STUDY_ALIAS")
    String alias;

    @Column(name="STUDY_XML")
    @Type(type="uk.ac.ebi.subs.ena.type.XMLType")
    Document document;

    @Column(name = "HOLD_DATE")
    Timestamp holdDate;

    public Study () {
        super();
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

    public Timestamp getHoldDate() {
        if (holdDate == null && getStatusId() == 4) {
            return new java.sql.Timestamp(System.currentTimeMillis());
        }
        return holdDate;
    }

    public void setHoldDate(Timestamp holdDate) {
        this.holdDate = holdDate;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }
}
