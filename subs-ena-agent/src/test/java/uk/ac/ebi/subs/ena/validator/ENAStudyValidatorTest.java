package uk.ac.ebi.subs.ena.validator;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.config.RabbitMQDependentTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 *
 * Created by karoly on 09/06/2017.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = {EnaAgentApplication.class})
@Transactional
@Category(RabbitMQDependentTest.class)
public class ENAStudyValidatorTest {

    @Autowired
    ENAStudyValidator enaAgentStudyValidator;

    private static final String CENTER_NAME = "test-team";
    private final String SUBMITTABLE_TYPE = Study.class.getSimpleName();

    @Test
    public void testExecuteSubmittableValidation () {

    }

}