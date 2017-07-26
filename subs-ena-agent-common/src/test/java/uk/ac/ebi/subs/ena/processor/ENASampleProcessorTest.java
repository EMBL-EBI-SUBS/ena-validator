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
import uk.ac.ebi.subs.data.submittable.ENASample;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.util.ArrayList;
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
        final ArrayList<SingleValidationResult> singleValidationResultList = new ArrayList<>();
        final ProcessingCertificate processingCertificate = enaSampleProcessor.processAndConvertSubmittable(sample, singleValidationResultList);
        assertThat(processingCertificate, is(equalTo(new ProcessingCertificate(sample, Archive.Ena, ProcessingStatusEnum.Received, sample.getAccession()))));
    }

}