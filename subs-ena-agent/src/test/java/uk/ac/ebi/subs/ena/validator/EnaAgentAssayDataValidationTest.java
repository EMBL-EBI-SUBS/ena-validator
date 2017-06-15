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
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Study;
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
public class EnaAgentAssayDataValidationTest {

    @Autowired
    EnaAgentRunValidator enaAgentAssayDataValidator;

    private static final String CENTER_NAME = "test-team";
    private final String SUBMITTABLE_TYPE = AssayData.class.getSimpleName();

    @Test
    public void returnsSuccessfullyWhenValidationEnvelopeContainsAValidAssayData() throws Exception {
        final AssayData assayData = createAssayData(CENTER_NAME);
        final String expectedValidationErrorMessage =
                String.format(EnaAgentValidator.SUCCESS_MESSAGE, SUBMITTABLE_TYPE);

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentAssayDataValidator.executeSubmittableValidation(assayData, enaAgentAssayDataValidator.getEnaRunProcessor());

        String validationMessage = enaAgentAssayDataValidator.assembleErrorMessage(validationMessages, SUBMITTABLE_TYPE);

        assertThat("There should be no validation messages", validationMessage, is(expectedValidationErrorMessage));
    }

    @Test
    public void returnsErrorMessagesWhenValidationEnvelopeContainsANullAssayData() throws Exception {
        final AssayData assayData = null;
        final String expectedValidationErrorMessage =
                String.format(EnaAgentValidator.NULL_SAMPLE_ERROR_MESSAGE, SUBMITTABLE_TYPE);
        final int expectedValidationMessageCount = 1;

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentAssayDataValidator.executeSubmittableValidation(assayData, enaAgentAssayDataValidator.getEnaRunProcessor());

        String validationMessage = enaAgentAssayDataValidator.assembleErrorMessage(validationMessages, SUBMITTABLE_TYPE);

        assertThat("Validation should fail with null assay data",
                validationMessage, is(expectedValidationErrorMessage));
        assertThat("Validation message count should be 1",
                validationMessages.size(), is(expectedValidationMessageCount));
    }

    private AssayData createAssayData(String centerName) {
        String alias = getAlias();
        final Team team = getTeam(centerName);
        AssayData assayData = TestHelper.getAssayData(alias, team, alias);
        assayData.setId(UUID.randomUUID().toString());
        return assayData;
    }

    private Team getTeam(String centerName) {
        return TestHelper.getTeam(centerName);
    }

    private String getAlias() {
        return UUID.randomUUID().toString();
    }
}