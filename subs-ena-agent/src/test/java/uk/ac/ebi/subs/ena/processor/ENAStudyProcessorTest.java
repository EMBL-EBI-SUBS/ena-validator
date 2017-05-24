package uk.ac.ebi.subs.ena.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * Created by neilg on 18/05/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class ENAStudyProcessorTest {

    @Autowired
    ENAStudyProcessor enaStudyProcessor;

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
        final Study study = TestHelper.getStudy(alias, team);
        study.setId(UUID.randomUUID().toString());
        Submission submission = new Submission();
        submission.setTeam(team);
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope(submission);
        submissionEnvelope.getStudies().add(study);
        final List<ProcessingCertificate> processingCertificateList = enaStudyProcessor.processSubmission(submissionEnvelope);
        assertThat("correct certs",
                processingCertificateList,
                containsInAnyOrder(
                        new ProcessingCertificate(study, Archive.Ena, ProcessingStatusEnum.Received, study.getAccession())
                )

        );
    }

}