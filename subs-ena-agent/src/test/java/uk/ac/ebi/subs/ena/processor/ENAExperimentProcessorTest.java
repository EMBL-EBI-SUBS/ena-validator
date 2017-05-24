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
import uk.ac.ebi.subs.data.submittable.ENAExperiment;
import uk.ac.ebi.subs.data.submittable.ENASample;
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
public class ENAExperimentProcessorTest {

    @Autowired
    ENAExperimentProcessor enaExperimentProcessor;

    @Autowired
    ENASampleProcessor enaSampleProcessor;

    @Autowired
    ENAStudyProcessor enaStudyProcessor;

    @Test
    public void process() throws Exception {
        String alias = UUID.randomUUID().toString();
        final Team team = TestHelper.getTeam("test-team");
        ENASampleProcessorTest.process(enaSampleProcessor,alias, team);
        ENAStudyProcessorTest.process(enaStudyProcessor,alias,team);
        final ENAExperiment enaExperiment = TestHelper.getENAExperiment(alias, team);
        final ProcessingCertificate processingCertificate = enaExperimentProcessor.process(enaExperiment);
        assertThat("experiment accessioned", enaExperiment.getAccession(), startsWith("ERX"));
        assertThat("correct certificate",processingCertificate.getProcessingStatus() , equalTo(ProcessingStatusEnum.Received));
    }

}