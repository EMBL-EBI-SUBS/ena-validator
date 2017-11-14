package uk.ac.ebi.subs.ena.loader;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import uk.ac.ebi.ena.sra.xml.*;

import java.io.*;
import java.net.URL;
import java.util.UUID;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static uk.ac.ebi.subs.ena.helper.TestHelper.*;

/**
 * Created by neilg on 22/05/2017.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = {EnaAgentApplication.class})
public class RunSRALoaderTest extends AbstractSRALoaderTest {


    @Value("${ena.ftp.url}")
    String enaFTPServerURL;

    File tempDir = null;

    String FASTQ_FILE_NAME = "test_forward.gz";

    String FASTQ_FILE = "/uk/ac/ebi/subs/ena/" + FASTQ_FILE_NAME;

    @Autowired
    StudySRALoader studySRALoader;

    @Autowired
    SampleSRALoader sampleSRALoader;

    @Autowired
    ExperimentSRALoader experimentSRALoader;

    @Autowired
    RunSRALoader runSRALoader;


    @Before
    public void setUp () throws Exception {
        tempDir = new File(System.getProperty("java.io.tmpdir"));
        //@TODO need to set this when we handles files
        //System.setProperty(SRAFileHandler.SRA_UPLOAD_DIR_PATH_PROPERTY, tempDir.getAbsolutePath());
        final URL url = getClass().getResource(FASTQ_FILE);
        File fastQFile = new File(url.toURI());
        File destinationFile = new File(tempDir,fastQFile.getName());
        copyFile(fastQFile,destinationFile);
    }

    @After
    public void finish () throws IOException {

    }



    private int copyFile (File sourceFile, File destinationFile) throws IOException {
        if (sourceFile.isFile() && !sourceFile.isHidden()) {
            InputStream inputStream = new FileInputStream(sourceFile);
            FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
            IOUtils.copy(inputStream, fileOutputStream);
            fileOutputStream.close();
            return 1;
        } else if (sourceFile.isDirectory()) {
            destinationFile.mkdirs();
            int fileCount = 0;
            for (String fileName : sourceFile.list()) {
                fileCount += copyFile(new File(sourceFile,fileName),new File(destinationFile,fileName));
            }
            return fileCount;
        } else {
            return 0;
        }
    }

    //@Test
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
        String experimentSubmissionXML = createSubmittable("experiment.xml", SubmissionType.ACTIONS.ACTION.ADD.Schema.EXPERIMENT,alias);
        experimentSRALoader.executeSRASubmission(experimentSubmissionXML, experimentsetDocument.xmlText());
        final String experimentAccession = sampleSRALoader.getAccession();
                assertThat(experimentAccession,startsWith("ERX"));

        RUNSETDocument runsetDocument = getRunSetDocument(alias,alias,getCenterName(),FASTQ_FILE_NAME,"fastq");
        String runSubmissionXML = createSubmittable("run.xml",SubmissionType.ACTIONS.ACTION.ADD.Schema.RUN,alias);
        runSRALoader.executeSRASubmission(runSubmissionXML,runsetDocument.xmlText());
        final String runAccession = runSRALoader.getAccession();
        assertThat(runAccession,startsWith("ERR"));
    }

}