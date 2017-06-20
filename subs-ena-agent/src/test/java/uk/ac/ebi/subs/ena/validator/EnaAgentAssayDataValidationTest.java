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
import uk.ac.ebi.subs.data.submittable.AssayData;
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
public class EnaAgentAssayDataValidationTest {

    @Autowired
    EnaAgentRunValidator enaAgentAssayDataValidator;

    private static final String CENTER_NAME = "test-team";
    private final String SUBMITTABLE_TYPE = AssayData.class.getSimpleName();

    // TODO check it with Neil
//    @Test
//    public void returnsSuccessfullyWhenValidationEnvelopeContainsAValidAssayData() throws Exception {
//        final AssayData assayData = ValidatorTestUtil.createAssayData(
//                "Run_2017_6_16_run0",
//                "Centre for Genomic Epidemiology, National Food Institute, Technical University of Denmark (DTU), Denmark",
//                "Exp_2017616_1");
//        final String expectedValidationErrorMessage =
//                String.format(EnaAgentValidator.SUCCESS_MESSAGE, SUBMITTABLE_TYPE);
//
//        Collection<ValidationMessage<Origin>> validationMessages =
//                enaAgentAssayDataValidator.executeSubmittableValidation(assayData, enaAgentAssayDataValidator.getEnaRunProcessor());
//
//        String validationMessage = enaAgentAssayDataValidator.assembleValidationMessage(validationMessages, SUBMITTABLE_TYPE);
//
//        assertThat("There should be no validation messages", validationMessage, is(expectedValidationErrorMessage));
//    }

    @Test
    public void returnsErrorMessagesWhenValidationEnvelopeContainsANullAssayData() throws Exception {
        final AssayData assayData = null;

        final ValidationMessage<Origin> nullEntityValidationMessage =
                ValidationMessage.error("ERAM.1.0.14", SUBMITTABLE_TYPE);
        final String expectedValidationErrorMessage = String.format(nullEntityValidationMessage.getMessage());

        final int expectedValidationMessageCount = 1;

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentAssayDataValidator.executeSubmittableValidation(assayData, enaAgentAssayDataValidator.getEnaRunProcessor());

        String validationMessage = enaAgentAssayDataValidator.assembleValidationMessage(validationMessages, SUBMITTABLE_TYPE);

        assertThat("Validation should fail with null assay data",
                validationMessage, is(expectedValidationErrorMessage));
        assertThat("Validation message count should be at least 1",
                validationMessages.size(), is(expectedValidationMessageCount));
    }
}