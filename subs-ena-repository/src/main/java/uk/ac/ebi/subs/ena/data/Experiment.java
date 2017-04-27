package uk.ac.ebi.subs.ena.data;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.w3c.dom.Document;

import javax.persistence.*;

@Entity
@Table(name="EXPERIMENT")
public class Experiment extends AbstractSubmittableSRAInfo<Experiment>  {

    @Id
    @GenericGenerator(name = "sraExperimentGen", strategy = "uk.ac.ebi.subs.ena.id.ExperimentIDGenerator")
    @GeneratedValue(generator = "sraExperimentGen")
    @Column(name = "EXPERIMENT_ID")
    String id;

    @Column(name = "EXPERIMENT_ALIAS")
    String alias;

    @Column(name="EXPERIMENT_XML")
    @Type(type="uk.ac.ebi.subs.ena.type.XMLType")
    Document document;

    public Experiment () {
        super();
    }

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
