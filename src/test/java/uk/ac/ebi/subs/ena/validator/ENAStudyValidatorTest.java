package uk.ac.ebi.subs.ena.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.config.RabbitMQDependentTest;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 *
 * Created by karoly on 09/06/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class ENAStudyValidatorTest {

    @Autowired
    ENAStudyValidator enaStudyValidator;

    SubmissionEnvelope submissionEnvelope;

    private static final String SUBMISSION_ID = "12ab34cd-1234-5678-9999-aabbccddeeff";

    @Before
    public void setUp() {
        submissionEnvelope = new SubmissionEnvelope();
        submissionEnvelope.setSubmission(enaStudyValidator.createSubmission(SUBMISSION_ID));
    }

    private static final String CENTER_NAME = "test-team";

    @Test
    public void testExecuteSubmittableValidation () {
        final Team team = TestHelper.getTeam(CENTER_NAME);
        final Study study = TestHelper.getStudy(UUID.randomUUID().toString(), team, "study_abstract","Whole Genome Sequencing");
        submissionEnvelope.getStudies().add(study);
        final List<SingleValidationResult> singleValidationResultList = enaStudyValidator.validate(submissionEnvelope,study);
        final SingleValidationResult singleValidationResult = singleValidationResultList.get(0);
        assertThat(singleValidationResult.getValidationStatus(), is(SingleValidationResultStatus.Pass));
    }

}