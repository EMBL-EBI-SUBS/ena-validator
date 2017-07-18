package uk.ac.ebi.subs.ena.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.ENAExperiment;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.processor.ENAExperimentProcessor;
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
public class MockEnaAgentAssayValidationTest {

    private static final String TEAM_NAME = "teamName";
    private static final String ASSAY_ALIAS = "assayAlias";
    private static final String SAMPLE_ALIAS = "sampleAlias";
    private static final String STUDY_ALIAS = "studyAlias";

    @Autowired
    ENAExperimentValidator enaAgentAssayValidator;

    private final String SUBMITTABLE_TYPE = Assay.class.getSimpleName();

    Collection<SingleValidationResult> singleValidationResultCollection = new ArrayList<>();

    @Test
    public void returnsSuccessfullyWhenValidationEnvelopeContainsAValidAssay() throws Exception {
        final Assay assay = ValidatorTestUtil.createAssay(ASSAY_ALIAS, TEAM_NAME, SAMPLE_ALIAS, STUDY_ALIAS);

        ENAExperimentProcessor mockedEnaExperimentProcessor = mock(ENAExperimentProcessor.class);
        enaAgentAssayValidator.setExperimentProcessor(mockedEnaExperimentProcessor);
        mockedEnaExperimentProcessor.validateEntity((ENAExperiment) mockedEnaExperimentProcessor.convertFromSubmittableToENASubmittable(assay,singleValidationResultCollection));
        assertTrue(singleValidationResultCollection.isEmpty());
    }

}
