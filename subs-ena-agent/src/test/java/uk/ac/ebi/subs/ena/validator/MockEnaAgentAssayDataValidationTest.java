package uk.ac.ebi.subs.ena.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.ValidationMessage;
import uk.ac.ebi.embl.api.validation.ValidationResult;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.ENARun;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.processor.ENARunProcessor;

import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by karoly on 19/06/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
@Transactional
public class MockEnaAgentAssayDataValidationTest {

    private static final String ASSAY_DATA_ALIAS = "assayDataAlias";
    private static final String TEAM_NAME = "teamName";
    private static final String ASSAY_ALIAS = "assayAlias";

    @Autowired
    EnaAgentRunValidator enaAgentAssayDataValidator;

    private final String SUBMITTABLE_TYPE = AssayData.class.getSimpleName();

    @Test
    public void returnsSuccessfullyWhenValidationEnvelopeContainsAValidAssayData() throws Exception {
        final AssayData assayData = ValidatorTestUtil.createAssayData(ASSAY_DATA_ALIAS, TEAM_NAME, ASSAY_ALIAS);

        ENARunProcessor mockedEnaRunProcessor = mock(ENARunProcessor.class);
        enaAgentAssayDataValidator.setEnaRunProcessor(mockedEnaRunProcessor);

        /*
        when(mockedEnaRunProcessor.validateEntity((ENARun) mockedEnaRunProcessor.convertFromSubmittableToENASubmittable(assayData)))
                .thenReturn(new ValidationResult().getMessages()
        );

        final String expectedValidationErrorMessage =
                String.format(EnaAgentValidator.SUCCESS_MESSAGE, SUBMITTABLE_TYPE);
        final int expectedValidationMessageCount = 0;

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentAssayDataValidator.executeSubmittableValidation(assayData, enaAgentAssayDataValidator.getEnaRunProcessor());

        String validationMessage = enaAgentAssayDataValidator.assembleValidationMessage(validationMessages, SUBMITTABLE_TYPE);

        assertThat("Message count should be 0 from validation services",
                validationMessages.size(), is(expectedValidationMessageCount));
        assertThat("There should be a success validation message from our service after checking the service response",
                validationMessage, is(expectedValidationErrorMessage));
                */
    }

    @Test
    public void returnsErrorMessagesWhenValidationEnvelopeContainsAnInvalidAssayData() throws Exception {
        final AssayData assayData = ValidatorTestUtil.createAssayData(ASSAY_DATA_ALIAS, TEAM_NAME, ASSAY_ALIAS);

        ENARunProcessor mockedEnaRunProcessor = mock(ENARunProcessor.class);
        enaAgentAssayDataValidator.setEnaRunProcessor(mockedEnaRunProcessor);
        /*

        final ValidationMessage<Origin> unknownEntityValidationMessage =
                ValidationMessage.error("ERAM.1.1.10", SUBMITTABLE_TYPE);

        when(mockedEnaRunProcessor.validateEntity(any()))
                .thenReturn(new ValidationResult().append(unknownEntityValidationMessage).getMessages());

        final String expectedValidationErrorMessage = String.format(unknownEntityValidationMessage.getMessage());
        final int expectedValidationMessageCount = 1;

        Collection<ValidationMessage<Origin>> validationMessages =
                enaAgentAssayDataValidator.executeSubmittableValidation(assayData, enaAgentAssayDataValidator.getEnaRunProcessor());

        String validationMessage = enaAgentAssayDataValidator.assembleValidationMessage(validationMessages, SUBMITTABLE_TYPE);

        assertThat("Message count should be at least 1 from validation services",
                validationMessages.size(), is(expectedValidationMessageCount));
        assertThat("Validation should fail with an error message",
                validationMessage, is(expectedValidationErrorMessage));
                */
    }
}
