package uk.ac.ebi.subs.ena.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.processor.ENAStudyProcessor;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.*;

/**
 * Created by karoly on 19/06/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class MockEnaAgentStudyValidationTest {

    private static final String TEAM_NAME = "teamName";

    @Autowired
    ENAStudyValidator enaAgentStudyValidator;

    Collection<SingleValidationResult> singleValidationResultCollection = new ArrayList<>();

    private final String SUBMITTABLE_TYPE = Study.class.getSimpleName();

    @Test
    public void returnsSuccessfullyWhenValidationEnvelopeContainsAValidStudy() throws Exception {
        final Study study = ValidatorTestUtil.createStudy(TEAM_NAME);

        ENAStudyProcessor mockedEnaStudyProcessor = mock(ENAStudyProcessor.class);
        enaAgentStudyValidator.setEnaStudyProcessor(mockedEnaStudyProcessor);
        mockedEnaStudyProcessor.validateEntity((ENAStudy) mockedEnaStudyProcessor.convertFromSubmittableToENASubmittable(study,singleValidationResultCollection));
    }

}
