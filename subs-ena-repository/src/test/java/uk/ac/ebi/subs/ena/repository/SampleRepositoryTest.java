package uk.ac.ebi.subs.ena.repository;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import uk.ac.ebi.ena.sra.xml.SAMPLESETDocument;
import uk.ac.ebi.ena.sra.xml.SampleSetType;
import uk.ac.ebi.ena.sra.xml.SampleType;
import uk.ac.ebi.subs.ena.ENATestRepositoryApplication;
import uk.ac.ebi.subs.ena.data.Sample;
import uk.ac.ebi.subs.ena.data.SubmissionStatus;

/**
 * Created by neilg on 25/04/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ENATestRepositoryApplication.class)
public class SampleRepositoryTest extends SubmittableSRARepositoryTest<Sample,SampleRepository> {


    @Override
    public String getId() {
        return "ERS452493";
    }

    @Override
    public String getAlias() {
        return "Salmonella_Typhi_E00-7866";
    }

    @Override
    public Sample createSubmittable(String alias, String submissionAccountId, SubmissionStatus submissionStatus) {
        final SAMPLESETDocument samplesetDocument = SAMPLESETDocument.Factory.newInstance();
        final SampleSetType sampleSetType = samplesetDocument.addNewSAMPLESET();
        final SampleType sampleType = sampleSetType.addNewSAMPLE();
        sampleType.setAlias(alias);
        Document sampleDocument = (Document) samplesetDocument.getDomNode();
        Sample sample = new Sample();
        sample.setDocument(sampleDocument);
        sample.setSubmissionStatus(submissionStatus);
        sample.setSubmissionAccountId(submissionAccountId);
        sample.updateMD5();
        return sample;
    }

    @Autowired
    void setSubmissionRepository(SampleRepository repository) {
        this.repository = repository;
    }
}