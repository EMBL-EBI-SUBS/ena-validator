package uk.ac.ebi.subs.ena.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import uk.ac.ebi.ena.sra.xml.EXPERIMENTSETDocument;
import uk.ac.ebi.ena.sra.xml.ExperimentType;
import uk.ac.ebi.subs.ena.data.Experiment;
import uk.ac.ebi.subs.ena.data.SubmissionStatus;

/**
 * Created by neilg on 26/04/2017.
 */



public class ExperimentRepositoryTest extends SubmittableSRARepositoryTest<Experiment,ExperimentRepository> {

    public ExperimentRepositoryTest() {}

    @Override
    public String getId() {
        createSubmittable(getAlias(),getSubmissionAccountId(),SubmissionStatus.PRIVATE);
        return "ERX002395";
    }

    @Override
    public String getAlias() {
        return "mouse-sc-LP-J-SLX-300-DSS-1-LP/J-76-2";
    }

    @Override
    public String getSubmissionId() {
        return "ERA000196";
    }

    @Override
    public Experiment createSubmittable(String alias, String submissionAccountId, SubmissionStatus submissionStatus) {
        final EXPERIMENTSETDocument experimentsetDocument = EXPERIMENTSETDocument.Factory.newInstance();
        final ExperimentType experimentType = experimentsetDocument.addNewEXPERIMENTSET().addNewEXPERIMENT();
        experimentType.setAlias(alias);
        experimentType.setCenterName("SC");
        Document experimentDocument = (Document)experimentsetDocument.getDomNode();
        Experiment experiment = new Experiment();
        experiment.setDocument(experimentDocument);
        experiment.setSubmissionStatus(submissionStatus);
        experiment.setSubmissionAccountId(submissionAccountId);
        experiment.updateMD5();
        return experiment;
    }


    @Autowired
    void setSubmissionRepository(ExperimentRepository repository) {
        this.repository = repository;
    }

}

