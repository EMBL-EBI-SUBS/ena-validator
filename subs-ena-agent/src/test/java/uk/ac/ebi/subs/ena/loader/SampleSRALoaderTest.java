package uk.ac.ebi.subs.ena.loader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.ena.sra.xml.SAMPLESETDocument;
import uk.ac.ebi.ena.sra.xml.SubmissionType;
import uk.ac.ebi.subs.data.submittable.ENASample;
import uk.ac.ebi.subs.ena.EnaAgentApplication;

import java.util.UUID;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static uk.ac.ebi.subs.ena.helper.TestHelper.getSamplesetDocument;

/**
 * Created by neilg on 17/05/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class SampleSRALoaderTest extends AbstractSRALoaderTest {
    @Autowired
    SampleSRALoader sraLoader;

    @Test
    public void executeSRALoader() throws Exception {
        String alias = UUID.randomUUID().toString();
        SAMPLESETDocument samplesetDocument = getSamplesetDocument(alias,getCenterName());
        String submissionXML = createSubmittable("sample.xml", SubmissionType.ACTIONS.ACTION.ADD.Schema.SAMPLE,alias);
        final String accession = sraLoader.executeSRALoader(submissionXML, samplesetDocument.xmlText(), connection);
        assertThat(accession,startsWith("ERS"));
    }

    @Test
    public void executeSubmittableSRALoader() throws Exception {
        String alias = UUID.randomUUID().toString();
        ENASample enaSample = new ENASample();
        enaSample.setAlias(alias);
        enaSample.setTeam(getTeam());
        enaSample.setTaxonId(9606l);
        enaSample.setId(UUID.randomUUID().toString());
        sraLoader.executeSubmittableSRALoader(enaSample,alias,connection);
        String accession = enaSample.getAccession();
        assertThat(accession,startsWith("ERS"));
    }

}
