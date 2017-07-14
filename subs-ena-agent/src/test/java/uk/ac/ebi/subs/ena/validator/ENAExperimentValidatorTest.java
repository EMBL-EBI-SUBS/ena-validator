package uk.ac.ebi.subs.ena.validator;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.config.RabbitMQDependentTest;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.util.List;

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
public class ENAExperimentValidatorTest {

    @Autowired
    ENAExperimentValidator enaAgentAssayValidator;

    private static final String CENTER_NAME = "test-team";
    private final String SUBMITTABLE_TYPE = Assay.class.getSimpleName();

    // TODO: check it with Neil
//    @Test
//    public void returnsSuccessfullyWhenValidationEnvelopeContainsAValidAssay() throws Exception {
//        final Assay assay = ValidatorTestUtil.createAssay(
//                "Input_300Frag",
//                "UU",
//                "Input",
//                "UU_HepG2_ChIP-seq_SOLiD");
//
//        final String expectedValidationErrorMessage =
//                String.format(EnaAgentValidator.SUCCESS_MESSAGE, SUBMITTABLE_TYPE);
//
//        Collection<ValidationMessage<Origin>> validationMessages =
//                enaAgentAssayValidator.executeSubmittableValidation(assay, enaAgentAssayValidator.getEnaExperimentProcessor());
//
//        String validationMessage = enaAgentAssayValidator.assembleValidationMessage(validationMessages, SUBMITTABLE_TYPE);
//
//        assertThat("There should be no validation messages", validationMessage, is(expectedValidationErrorMessage));
//    }

    @Test
    public void returnsSErrorMessagesWhenValidationEnvelopeContainsAnAssayWithUnknownSampleAndUnknownStudy() throws Exception {
        final String assayAlias = "assayAlias";
        final String samplaAlias = "samplaAlias";
        final String studyAlias = "studyAlias";
        final Assay assay = ValidatorTestUtil.createAssay(
                assayAlias,
                CENTER_NAME,
                samplaAlias,
                studyAlias);
        final String expectedValidationErrorMessage =
                String.format(String.format("Unknown study ( name:%s) in experiment( name:%s) , Unknown sample ( name:%s) in experiment( name:%s) ",
                        studyAlias, assayAlias, samplaAlias, assayAlias));
        final int expectedValidationMessageCount = 2;

        final List<SingleValidationResult> singleValidationResults = enaAgentAssayValidator.executeSubmittableValidation(assay, enaAgentAssayValidator.getEnaExperimentProcessor());

        /*
        String validationMessage = enaAgentAssayValidator.assembleValidationMessage(validationMessages, SUBMITTABLE_TYPE);

        assertThat("Validation should fail with null assay data",
                validationMessage, is(expectedValidationErrorMessage));
        assertThat("Validation message count should be 2",
                validationMessages.size(), is(expectedValidationMessageCount));
                */
    }

    // this error scenerio will be covered by the core validator
    /*
    @Test
    public void returnsErrorMessagesWhenValidationEnvelopeContainsANullAssay() throws Exception {
        final Assay assay = null;
        final String expectedValidationErrorMessage =
                String.format(EnaAgentValidator.NULL_SAMPLE_ERROR_MESSAGE, SUBMITTABLE_TYPE);
        final int expectedValidationMessageCount = 1;

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentAssayValidator.executeSubmittableValidation(assay, enaAgentAssayValidator.getEnaExperimentProcessor());

        String validationMessage = enaAgentAssayValidator.assembleValidationMessage(validationMessages, SUBMITTABLE_TYPE);

        assertThat("Validation should fail with null assay data",
                validationMessage, is(expectedValidationErrorMessage));
        assertThat("Validation message count should be 1",
                validationMessages.size(), is(expectedValidationMessageCount));
    }
    */
}