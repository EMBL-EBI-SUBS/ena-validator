package uk.ac.ebi.subs.ena.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.ValidationMessage;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.ena.validator.EnaAgentSampleValidator;
import uk.ac.ebi.subs.ena.validator.EnaAgentStudyValidator;

import java.util.Collection;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 *
 * Created by karoly on 09/06/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
@Transactional
public class EnaAgentStudyValidationTest {

    @Autowired
    EnaAgentStudyValidator enaAgentStudyValidator;

    private static final String CENTER_NAME = "test-team";
    private final String SUBMITTABLETYPE = Study.class.getSimpleName();

    @Test
    public void returnsSuccessfullyWhenValidationEnvelopeContainsAValidStudy() throws Exception {
        final Study study = createStudy(CENTER_NAME);
        final String expectedValidationErrorMessage =
                String.format(enaAgentStudyValidator.SUCCESS_MESSAGE, SUBMITTABLETYPE);

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentStudyValidator.executeSubmittableValidation(study, enaAgentStudyValidator.getEnaStudyProcessor());

        String validationMessage = enaAgentStudyValidator.assembleErrorMessage(validationMessages, SUBMITTABLETYPE);


        assertThat("There should be no validation messages", validationMessage, is(expectedValidationErrorMessage));
    }

    @Test
    public void returnsErrorMessagesWhenValidationEnvelopeContainsANullSample() throws Exception {
        final Sample sample = null;
        final String expectedValidationErrorMessage =
                String.format(enaAgentStudyValidator.NULL_SAMPLE_ERROR_MESSAGE, SUBMITTABLETYPE);
        final int expectedValidationMessageCount = 1;

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentStudyValidator.executeSubmittableValidation(sample, enaAgentStudyValidator.getEnaStudyProcessor());

        String validationMessage = enaAgentStudyValidator.assembleErrorMessage(validationMessages, SUBMITTABLETYPE);

        assertThat("Validation should fail with null study",
                validationMessage, is(expectedValidationErrorMessage));
        assertThat("Validation message count should be 1",
                validationMessages.size(), is(expectedValidationMessageCount));

    }

    private Sample createSample() {
        String alias = getAlias();
        final Team team = getTeam(CENTER_NAME);
        return TestHelper.getSample(alias, team);
    }

    private Study createStudy(String centerName) {
        String alias = getAlias();
        final Team team = getTeam(centerName);
        return TestHelper.getStudy(alias, team);
    }

    private Team getTeam(String centerName) {
        return TestHelper.getTeam(centerName);
    }

    private String getAlias() {
        return UUID.randomUUID().toString();
    }
}