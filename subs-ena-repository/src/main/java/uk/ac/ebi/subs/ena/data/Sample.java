package uk.ac.ebi.subs.ena.data;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.w3c.dom.Document;

import javax.persistence.*;

/**
 * Created by neilg on 25/04/2017.
 */
@Entity
@Table(name="SAMPLE")
public class Sample extends AbstractSubmittable<Sample> {

    @Id
    @GenericGenerator(name = "sraSampleGen", strategy = "uk.ac.ebi.subs.ena.id.SampleIDGenerator")
    @GeneratedValue(generator = "sraSampleGen")
    @Column(name = "SAMPLE_ID")
    String id;

    @Column(name = "SAMPLE_ALIAS")
    String alias;

    @Column(name="SAMPLE_XML")
    @Type(type="uk.ac.ebi.subs.ena.type.XMLType")
    Document document;

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(String id) {

    }

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public Document getDocument() {
        return null;
    }

    @Override
    public void setDocument(Document document) {

    }
}
