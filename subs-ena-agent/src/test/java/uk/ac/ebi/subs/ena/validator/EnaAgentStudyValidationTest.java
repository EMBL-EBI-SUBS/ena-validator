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
import uk.ac.ebi.subs.data.submittable.Study;
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
public class EnaAgentStudyValidationTest {

    @Autowired
    EnaAgentStudyValidator enaAgentStudyValidator;

    private static final String CENTER_NAME = "test-team";
    private final String SUBMITTABLE_TYPE = Study.class.getSimpleName();

    @Test
    public void returnsSuccessfullyWhenValidationEnvelopeContainsAValidStudy() throws Exception {
        final Study study = ValidatorTestUtil.createStudy(CENTER_NAME);
        final String expectedValidationErrorMessage =
                String.format(EnaAgentValidator.SUCCESS_MESSAGE, SUBMITTABLE_TYPE);

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentStudyValidator.executeSubmittableValidation(study, enaAgentStudyValidator.getEnaStudyProcessor());

        String validationMessage = enaAgentStudyValidator.assembleValidationMessage(validationMessages, SUBMITTABLE_TYPE);

        assertThat("There should be no validation messages", validationMessage, is(expectedValidationErrorMessage));
    }

    @Test
    public void returnsErrorMessagesWhenValidationEnvelopeContainsANullStudy() throws Exception {
        final Study study = null;
        final String expectedValidationErrorMessage =
                String.format(EnaAgentValidator.NULL_SAMPLE_ERROR_MESSAGE, SUBMITTABLE_TYPE);
        final int expectedValidationMessageCount = 1;

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentStudyValidator.executeSubmittableValidation(study, enaAgentStudyValidator.getEnaStudyProcessor());

        String validationMessage = enaAgentStudyValidator.assembleValidationMessage(validationMessages, SUBMITTABLE_TYPE);

        assertThat("Validation should fail with null study",
                validationMessage, is(expectedValidationErrorMessage));
        assertThat("Validation message count should be 1",
                validationMessages.size(), is(expectedValidationMessageCount));
    }
}