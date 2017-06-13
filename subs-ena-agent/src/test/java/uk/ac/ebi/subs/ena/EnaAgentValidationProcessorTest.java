package uk.ac.ebi.subs.ena;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.ValidationMessage;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.ena.processor.ENASampleProcessor;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;

import java.util.Collection;
import java.util.List;
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
public class EnaAgentValidationProcessorTest {

    @Autowired
    EnaAgentValidator enaAgentValidator;

    @Test
    public void returnsSuccessfullyWhenValidationEnvelopeContainsAValidSample() throws Exception {
        final Sample sample = createSample();
        final String expectedValidationErrorMessage = EnaAgentValidator.SUCCESS_MESSAGE;

        Collection<ValidationMessage<Origin>> validationMessages = enaAgentValidator.executeValidation(sample);

        String validationMessage = enaAgentValidator.assembleErrorMessage(validationMessages);


        assertThat("There should be no validation messages", validationMessage, is(expectedValidationErrorMessage));
    }

    @Test
    public void returnsErrorMessagesWhenValidationEnvelopeContainsANullSample() throws Exception {
        final Sample sample = null;
        final String expectedValidationErrorMessage = EnaAgentValidator.NULL_SAMPLE_ERROR_MESSAGE;
        final int expectedValidationMessageCount = 1;

        Collection<ValidationMessage<Origin>> validationMessages = enaAgentValidator.executeValidation(sample);

        String validationMessage = enaAgentValidator.assembleErrorMessage(validationMessages);

        assertThat("Validation should fail with null sample",
                validationMessage, is(expectedValidationErrorMessage));
        assertThat("Validation message count should be 1",
                validationMessages.size(), is(expectedValidationMessageCount));

    }

    private Sample createSample() {
        String alias = UUID.randomUUID().toString();
        final Team team = TestHelper.getTeam("test-team");
        return TestHelper.getSample(alias, team);
    }
}