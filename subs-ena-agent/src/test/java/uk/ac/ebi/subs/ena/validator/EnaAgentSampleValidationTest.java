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
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.ena.validator.EnaAgentSampleValidator;

import java.util.Collection;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 *
 * Created by karoly on 09/06/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
@Transactional
public class EnaAgentSampleValidationTest {

    @Autowired
    EnaAgentSampleValidator enaAgentSampleValidator;

    @Test
    public void returnsSuccessfullyWhenValidationEnvelopeContainsAValidSample() throws Exception {
        final Sample sample = createSample();
        final String submittableType = sample.getClass().getSimpleName();
        final String expectedValidationErrorMessage = String.format(EnaAgentSampleValidator.SUCCESS_MESSAGE, submittableType);

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentSampleValidator.executeSubmittableValidation(sample, enaAgentSampleValidator.getEnaSampleProcessor());

        String validationMessage = enaAgentSampleValidator.assembleErrorMessage(validationMessages, submittableType);


        assertThat("There should be no validation messages", validationMessage, is(expectedValidationErrorMessage));
    }

    @Test
    public void returnsErrorMessagesWhenValidationEnvelopeContainsANullSample() throws Exception {
        final Sample sample = null;
        final String submittableType = Sample.class.getSimpleName();
        final String expectedValidationErrorMessage =
                String.format(EnaAgentSampleValidator.NULL_SAMPLE_ERROR_MESSAGE, submittableType);
        final int expectedValidationMessageCount = 1;

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentSampleValidator.executeSubmittableValidation(sample, enaAgentSampleValidator.getEnaSampleProcessor());

        String validationMessage = enaAgentSampleValidator.assembleErrorMessage(validationMessages, submittableType);

        assertThat("Validation should fail with null sample",
                validationMessage, is(expectedValidationErrorMessage));
        assertThat("Validation message count should be 1",
                validationMessages.size(), is(expectedValidationMessageCount));

    }

    private Sample createSample() {
        String alias = getAlias();
        final Team team = getTeam("test-team");
        return TestHelper.getSample(alias, team);
    }

    private Team getTeam(String centerName) {
        return TestHelper.getTeam(centerName);
    }

    private String getAlias() {
        return UUID.randomUUID().toString();
    }
}