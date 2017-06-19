package uk.ac.ebi.subs.ena.validator;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.ValidationMessage;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.config.RabbitMQDependentTest;

import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 *
 * Created by karoly on 09/06/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
@Transactional
@Category(RabbitMQDependentTest.class)
public class EnaAgentSampleValidationTest {

    @Autowired
    EnaAgentSampleValidator enaAgentSampleValidator;

    @Test
    public void returnsSuccessfullyWhenValidationEnvelopeContainsAValidSample() throws Exception {
        final Sample sample = ValidatorTestUtil.createSample();
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
}