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

}