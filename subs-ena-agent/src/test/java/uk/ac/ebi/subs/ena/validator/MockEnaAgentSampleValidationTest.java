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
import uk.ac.ebi.subs.data.submittable.ENAExperiment;
import uk.ac.ebi.subs.data.submittable.ENASample;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.processor.ENASampleProcessor;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by karoly on 19/06/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
@Transactional
public class MockEnaAgentSampleValidationTest {

    @Autowired
    EnaAgentSampleValidator enaAgentSampleValidator;

    Collection<SingleValidationResult> singleValidationResultCollection = new ArrayList<>();

    private final String SUBMITTABLE_TYPE = Sample.class.getSimpleName();

    @Test
    public void returnsSuccessfullyWhenValidationEnvelopeContainsAValidSample() throws Exception {
        final Sample sample = ValidatorTestUtil.createSample();

        ENASampleProcessor mockedEnaSampleProcessor = mock(ENASampleProcessor.class);
        enaAgentSampleValidator.setEnaSampleProcessor(mockedEnaSampleProcessor);
        mockedEnaSampleProcessor.validateEntity((ENASample) mockedEnaSampleProcessor.convertFromSubmittableToENASubmittable(sample,singleValidationResultCollection));
        assertTrue(singleValidationResultCollection.isEmpty());
    }

}
