package uk.ac.ebi.subs.ena.processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by neilg on 18/05/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
@Transactional
public class ENAStudyProcessorTest {

    @Autowired
    ENAStudyProcessor enaStudyProcessor;

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void process () throws Exception {
        String alias = UUID.randomUUID().toString();
        final Team team = TestHelper.getTeam("test-team");
        process(enaStudyProcessor,alias,team);
    }

    static void process(ENAStudyProcessor enaStudyProcessor, String alias, Team team) throws Exception {
        final ENAStudy enaStudy = TestHelper.getENAStudy(alias, team);
        final ProcessingCertificate processingCertificate = enaStudyProcessor.process(enaStudy);
        assertThat("study accessioned", enaStudy.getAccession(), startsWith("ERP"));
        assertThat("correct certificate",processingCertificate.getProcessingStatus() , equalTo(ProcessingStatusEnum.Received));
    }

    @Test
    public void processSubmission() throws Exception {
        String alias = UUID.randomUUID().toString();
        final Team team = TestHelper.getTeam("test-team");
        final Study study = TestHelper.getStudy(alias, team,"study_abstract", "Whole Genome Sequencing");
        String releaseDate = "2018-12-25";
        final Date date = simpleDateFormat.parse(releaseDate);
        study.setReleaseDate(date);
        study.setId(UUID.randomUUID().toString());
        final ArrayList<SingleValidationResult> singleValidationResultList = new ArrayList<>();
        final ProcessingCertificate processingCertificate = enaStudyProcessor.processAndConvertSubmittable(study, singleValidationResultList);
        assertThat(processingCertificate, is(equalTo(new ProcessingCertificate(study, Archive.Ena, ProcessingStatusEnum.Received, study.getAccession()))));
    }

}