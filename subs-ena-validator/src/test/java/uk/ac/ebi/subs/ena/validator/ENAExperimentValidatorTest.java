package uk.ac.ebi.subs.ena.validator;

import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.ena.config.RabbitMQDependentTest;

/**
 *
 * Created by karoly on 09/06/2017.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = {EnaAgentApplication.class})
@Category(RabbitMQDependentTest.class)
public class ENAExperimentValidatorTest {

    @Autowired
    ENAExperimentValidator enaAgentAssayValidator;

    private static final String CENTER_NAME = "test-team";
    private final String SUBMITTABLE_TYPE = Assay.class.getSimpleName();

}