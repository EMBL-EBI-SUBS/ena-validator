package uk.ac.ebi.subs.ena.processor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.oxm.Marshaller;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.ena.sra.SRALoader;
import uk.ac.ebi.ena.sra.xml.SubmissionType;
import uk.ac.ebi.subs.EnaAgentApplication;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.status.ProcessingStatus;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.BaseSubmittable;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.data.submittable.TestStudyFactory;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by neilg on 10/04/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class ENAStudyProcessorTest {
    private static final Logger logger = LoggerFactory.getLogger(ENAStudyProcessorTest.class);

    @Autowired
    DataSource dataSource;

    Connection connection = null;

    static final String MY_TEAM_NAME = "my-team";

    Team team = null;
    Study study = null;
    String alias;

    @Autowired
    @Qualifier("study")
    Marshaller marshaller;
    ENAStudyProcessor enaStudyProcessor = null;

    @Value("${ena.submission_account_id}")
    String submissionAccountId;
    SubmissionEnvelope submissionEnvelope = null;

    @Before
    public void setUp() throws Exception {
        connection = dataSource.getConnection();
        team = new Team();
        team.setName(MY_TEAM_NAME);
        alias = UUID.randomUUID().toString();
        submissionEnvelope = createSubmissionEnvelope(alias,team);
        enaStudyProcessor = new ENAStudyProcessor(submissionEnvelope, marshaller, connection, submissionAccountId, SRALoader.TransactionMode.TRANSACTIONAL_WITH_ROLLBACK);

    }

    @After
    public void tearDown() throws Exception {
        connection.rollback();
        connection.close();
    }

    @Test
    public void addActions() throws Exception {
        SubmissionType submissionType = SubmissionType.Factory.newInstance();
        ENAStudy enaStudy = new ENAStudy(submissionEnvelope.getStudies().get(0));
        enaStudyProcessor.addActions(submissionType,enaStudy);
        String xmlText = submissionType.xmlText();
        // execute xpath query
        logger.info(xmlText);
    }

    @Test
    public void loadData() throws Exception {
        for (Study st : submissionEnvelope.getStudies()) {
            ENAStudy enaStudy = new ENAStudy(st);
            final ProcessingStatus processingStatus = enaStudyProcessor.loadData(enaStudy, submissionEnvelope);
            assertThat("processing status", processingStatus.getStatus(), equalTo(ProcessingStatusEnum.Accepted.toString()));
        }
    }

    @Test
    public void executeSRALoader() throws Exception {
    }

    @Test
    public void getBaseSubmittables() throws Exception {
        final SubmissionEnvelope submissionEnvelope = createSubmissionEnvelope(UUID.randomUUID().toString(), team);
        final List<? extends BaseSubmittable> baseSubmittableList = enaStudyProcessor.getBaseSubmittables(submissionEnvelope);
        for (BaseSubmittable baseSubmittable : baseSubmittableList) {
            logger.info(baseSubmittable.getId());
        }
    }

    SubmissionEnvelope createSubmissionEnvelope (String alias, Team test) {
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();
        for (int i = 0; i < 100; i++) {
            submissionEnvelope.getStudies().add(TestStudyFactory.createStudy(team));
        }
        return submissionEnvelope;
    }

}