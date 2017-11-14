package uk.ac.ebi.subs.ena.loader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.ena.sra.xml.EXPERIMENTSETDocument;
import uk.ac.ebi.ena.sra.xml.SAMPLESETDocument;
import uk.ac.ebi.ena.sra.xml.STUDYSETDocument;
import uk.ac.ebi.ena.sra.xml.SubmissionType;
import uk.ac.ebi.ena.sra.xml.SubmissionType.ACTIONS.ACTION.ADD.Schema;
import uk.ac.ebi.subs.ena.EnaAgentApplication;

import java.util.UUID;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static uk.ac.ebi.subs.ena.helper.TestHelper.*;

/**
 * Created by neilg on 17/05/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class ExperimentSRALoaderTest extends AbstractSRALoaderTest {
    @Autowired
    StudySRALoader studySRALoader;

    @Autowired
    SampleSRALoader sampleSRALoader;

    @Autowired
    ExperimentSRALoader experimentSRALoader;

    @Test
    public void executeSRALoader() throws Exception {
        String alias = UUID.randomUUID().toString();
        STUDYSETDocument studysetDocument = getStudysetDocument(alias,getCenterName());
        String studySubmissionXML = createSubmittable("study.xml", SubmissionType.ACTIONS.ACTION.ADD.Schema.STUDY,alias + "_study");
        studySRALoader.executeSRASubmission(studySubmissionXML, studysetDocument.xmlText());
        final String studyAccession = studySRALoader.getAccession();

        SAMPLESETDocument samplesetDocument = getSamplesetDocument(alias,getCenterName());
        String sampleSubmissionXML = createSubmittable("sample.xml", SubmissionType.ACTIONS.ACTION.ADD.Schema.SAMPLE,alias + "sample");
        sampleSRALoader.executeSRASubmission(sampleSubmissionXML, samplesetDocument.xmlText());
        final String sampleAccession = sampleSRALoader.getAccession();
        EXPERIMENTSETDocument experimentsetDocument = getExperimentSetDocument(alias,alias,alias,getCenterName());

        String experimentSubmissionXML = createSubmittable("experiment.xml", Schema.EXPERIMENT,alias);
        experimentSRALoader.executeSRASubmission(experimentSubmissionXML, experimentsetDocument.xmlText());
        final String experimentAccession = experimentSRALoader.getAccession();
        assertThat(experimentAccession,startsWith("ERX"));
    }



}