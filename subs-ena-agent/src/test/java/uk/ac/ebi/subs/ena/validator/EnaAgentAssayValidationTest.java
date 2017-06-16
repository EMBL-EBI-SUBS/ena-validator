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
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.helper.TestHelper;

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
public class EnaAgentAssayValidationTest {

    @Autowired
    EnaAgentExperimentValidator enaAgentAssayValidator;

    private static final String CENTER_NAME = "test-team";
    private final String SUBMITTABLE_TYPE = Assay.class.getSimpleName();

    @Test
    public void returnsSuccessfullyWhenValidationEnvelopeContainsAValidAssay() throws Exception {
        final Assay assay = createAssay(CENTER_NAME);
        final String expectedValidationErrorMessage =
                String.format(EnaAgentValidator.SUCCESS_MESSAGE, SUBMITTABLE_TYPE);

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentAssayValidator.executeSubmittableValidation(assay, enaAgentAssayValidator.getExperimentProcessor());

        String validationMessage = enaAgentAssayValidator.assembleErrorMessage(validationMessages, SUBMITTABLE_TYPE);

        assertThat("There should be no validation messages", validationMessage, is(expectedValidationErrorMessage));
    }

    @Test
    public void returnsErrorMessagesWhenValidationEnvelopeContainsANullAssay() throws Exception {
        final Assay assay = null;
        final String expectedValidationErrorMessage =
                String.format(EnaAgentValidator.NULL_SAMPLE_ERROR_MESSAGE, SUBMITTABLE_TYPE);
        final int expectedValidationMessageCount = 1;

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentAssayValidator.executeSubmittableValidation(assay, enaAgentAssayValidator.getExperimentProcessor());

        String validationMessage = enaAgentAssayValidator.assembleErrorMessage(validationMessages, SUBMITTABLE_TYPE);

        assertThat("Validation should fail with null assay data",
                validationMessage, is(expectedValidationErrorMessage));
        assertThat("Validation message count should be 1",
                validationMessages.size(), is(expectedValidationMessageCount));
    }

    private Assay createAssay(String centerName) {
        String alias = getAlias();
        final Team team = getTeam(centerName);
        Assay assay = TestHelper.getAssay(alias, team, alias, alias);
        assay.setId(UUID.randomUUID().toString());
        return assay;
    }

    private Team getTeam(String centerName) {
        return TestHelper.getTeam(centerName);
    }

    private String getAlias() {
        return UUID.randomUUID().toString();
    }
}