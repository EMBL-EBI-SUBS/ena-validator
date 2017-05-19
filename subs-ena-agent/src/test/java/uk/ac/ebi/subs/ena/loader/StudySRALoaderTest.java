package uk.ac.ebi.subs.ena.loader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.ena.sra.xml.*;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.ena.EnaAgentApplication;

import java.util.UUID;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.*;
import static uk.ac.ebi.subs.ena.helper.TestHelper.getENAStudy;
import static uk.ac.ebi.subs.ena.helper.TestHelper.getStudysetDocument;

/**
 * Created by neilg on 17/05/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class StudySRALoaderTest extends AbstractSRALoaderTest{

    @Autowired
    StudySRALoader sraLoader;

    @Test
    public void executeSRALoader() throws Exception {
        String alias = UUID.randomUUID().toString();
        STUDYSETDocument studysetDocument = getStudysetDocument(alias,getCenterName());
        String submissionXML = createSubmittable("study.xml", SubmissionType.ACTIONS.ACTION.ADD.Schema.STUDY,alias);
        final String accession = sraLoader.executeSRALoader(submissionXML, studysetDocument.xmlText(), connection);
        assertThat(accession,startsWith("ERP"));
    }

    @Test
    public void executeSubmittableSRALoader() throws Exception {
        String alias = UUID.randomUUID().toString();
        final ENAStudy enaStudy = getENAStudy(alias, getTeam());
        sraLoader.executeSubmittableSRALoader(enaStudy,alias,connection);
        String accession = enaStudy.getAccession();
        assertThat(accession,startsWith("ERP"));
    }

}