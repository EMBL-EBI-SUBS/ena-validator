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
import uk.ac.ebi.subs.data.submittable.ENASample;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.data.submittable.Sample;
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
public class ENASampleProcessorTest {

    @Autowired
    ENASampleProcessor enaSampleProcessor;

    public void setEnaSampleProcessor () throws Exception {
        String alias = UUID.randomUUID().toString();
        final Team team = TestHelper.getTeam("test-team");
        process(enaSampleProcessor,alias,team);
    }

    static void process(ENASampleProcessor enaSampleProcessor, String alias, Team team) throws Exception {
        final ENASample enaSample = TestHelper.getENASample(alias, team);
        final ProcessingCertificate processingCertificate = enaSampleProcessor.process(enaSample);
        assertThat("study accessioned", enaSample.getAccession(), startsWith("ERS"));
        assertThat("correct certificate",processingCertificate.getProcessingStatus() , equalTo(ProcessingStatusEnum.Received));
    }

    @Test
    public void processSubmission() throws Exception {
        String alias = UUID.randomUUID().toString();
        final Team team = TestHelper.getTeam("test-team");
        final Sample sample = TestHelper.getSample(alias, team);
        sample.setId(UUID.randomUUID().toString());
        Submission submission = new Submission();
        submission.setTeam(team);
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope(submission);
        submissionEnvelope.getSamples().add(sample);
        final List<ProcessingCertificate> processingCertificateList = enaSampleProcessor.processSubmission(submissionEnvelope);
        assertThat("correct certs",
                processingCertificateList,
                containsInAnyOrder(
                        new ProcessingCertificate(sample, Archive.Ena, ProcessingStatusEnum.Received, sample.getAccession())
                )

        );
    }

}