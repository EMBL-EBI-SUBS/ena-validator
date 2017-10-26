package uk.ac.ebi.subs.ena.validator;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.config.RabbitMQDependentTest;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.ena.processor.ENAStudyProcessor;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 *
 * Created by karoly on 09/06/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
@Transactional
@Category(RabbitMQDependentTest.class)
public class ENAStudyValidatorTest {

    @Autowired
    ENAStudyValidator enaAgentStudyValidator;

    @Autowired
    ENAStudyProcessor enaStudyProcessor;

    private static final String CENTER_NAME = "test-team";
    private final String SUBMITTABLE_TYPE = Study.class.getSimpleName();

    @Test
    public void testExecuteSubmittableValidation () {
        final Team team = TestHelper.getTeam("my-team");
        final Study study = TestHelper.getStudy(UUID.randomUUID().toString(), team, "study_abstract","Whole Genome Sequencing");
        final List<SingleValidationResult> singleValidationResults = enaAgentStudyValidator.executeSubmittableValidation(study, enaStudyProcessor);
        assertTrue("singleValidationResult",singleValidationResults.isEmpty());
    }

    @Test
    public void testMissingReleaseDate () {
        final Team team = TestHelper.getTeam("my-team");
        final Study study = TestHelper.getStudy(UUID.randomUUID().toString(), team, "study_abstract","Whole Genome Sequencing");
        final List<SingleValidationResult> singleValidationResults = new ArrayList<>();
        enaAgentStudyValidator.checkReleaseDate(study,singleValidationResults);
        assertTrue("singleValidationResult",singleValidationResults.size() == 1);
    }

    @Test
    public void testReleaseDateIntevalExceeded () {
        final Team team = TestHelper.getTeam("my-team");
        final Study study = TestHelper.getStudy(UUID.randomUUID().toString(), team, "study_abstract","Whole Genome Sequencing");
        final List<SingleValidationResult> singleValidationResults = new ArrayList<>();
        LocalDate dateTime = LocalDate.now();
        LocalDate dateTimePlusOneDay = dateTime.plusDays(ENAStudyValidator.RELEASE_DATE_INTERVAL_DAYS + 1);
        study.setReleaseDate(dateTimePlusOneDay);
        enaAgentStudyValidator.checkReleaseDate(study,singleValidationResults,ENAStudyValidator.RELEASE_DATE_INTERVAL_DAYS);
        assertTrue("singleValidationResult",singleValidationResults.size() == 1);
    }

    @Test
    public void testValidReleaseDate () {
        final Team team = TestHelper.getTeam("my-team");
        final Study study = TestHelper.getStudy(UUID.randomUUID().toString(), team, "study_abstract","Whole Genome Sequencing");
        final List<SingleValidationResult> singleValidationResults = new ArrayList<>();
        LocalDate dateTime = LocalDate.now();
        LocalDate dateTimePlusOneDay = dateTime.plusDays(ENAStudyValidator.RELEASE_DATE_INTERVAL_DAYS - 1);
        study.setReleaseDate(dateTimePlusOneDay);
        enaAgentStudyValidator.checkReleaseDate(study,singleValidationResults,ENAStudyValidator.RELEASE_DATE_INTERVAL_DAYS);
        assertTrue("singleValidationResult",singleValidationResults.isEmpty());
    }

    @Test
    public void testValidPastReleaseDate () {
        final Team team = TestHelper.getTeam("my-team");
        final Study study = TestHelper.getStudy(UUID.randomUUID().toString(), team, "study_abstract","Whole Genome Sequencing");
        final List<SingleValidationResult> singleValidationResults = new ArrayList<>();
        LocalDate dateTime = LocalDate.now();
        LocalDate dateTimePlusOneDay = dateTime.minusDays(ENAStudyValidator.RELEASE_DATE_INTERVAL_DAYS - 1);
        study.setReleaseDate(dateTimePlusOneDay);
        enaAgentStudyValidator.checkReleaseDate(study,singleValidationResults,ENAStudyValidator.RELEASE_DATE_INTERVAL_DAYS);
        assertTrue("singleValidationResult",singleValidationResults.isEmpty());
    }
}