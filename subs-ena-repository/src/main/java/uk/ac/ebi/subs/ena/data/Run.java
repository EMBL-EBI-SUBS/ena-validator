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
@Table(name="RUN")
public class Run extends AbstractSubmittable<Run>{

    @Id
    @GenericGenerator(name = "sraRunGen", strategy = "uk.ac.ebi.subs.ena.id.RunIDGenerator")
    @GeneratedValue(generator = "sraRunGen")
    @Column(name = "RUN_ID")
    String id;

    @Column(name = "RUN_ALIAS")
    String alias;

    @Column(name="RUN_XML")
    @Type(type="uk.ac.ebi.subs.ena.type.XMLType")
    Document document;

    public Run(String id, Document document, String submissionId, int statusId) {
        super(submissionId, statusId);
        this.id = id;
        this.document = document;
    }

    public Run () {
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

    @Override
    public String getAlias() {
        return alias;
    }
}
