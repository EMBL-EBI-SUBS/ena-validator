package uk.ac.ebi.subs.ena.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.utils.test.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static uk.ac.ebi.subs.ena.config.EnaValidatorRoutingKeys.EVENT_VALIDATION_SUCCESS;

/**
 *
 * Created by karoly on 09/06/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class ENAAssayDataValidatorTest {

    @Autowired
    ENAAssayDataValidator enaAssayDataValidator;

    @MockBean
    RabbitMessagingTemplate rabbitMessagingTemplate;

    SubmissionEnvelope submissionEnvelope;

    private static final String SUBMISSION_ID = "12ab34cd-1234-5678-9999-aabbccddeeff";

    @Before
    public void setUp() {
        submissionEnvelope = new SubmissionEnvelope();
        submissionEnvelope.setSubmission(enaAssayDataValidator.createSubmission(SUBMISSION_ID));
    }

    private static final String CENTER_NAME = "test-team";

    @Test
    public void testExecuteSubmittableValidation () {
        final Team team = TestHelper.getTeam(CENTER_NAME);
        final String alias = UUID.randomUUID().toString();
        final String submissionId = UUID.randomUUID().toString();

        final Study study = TestHelper.getStudy(
                alias, team, "study_abstract","Whole Genome Sequencing");
        final Sample sample = TestHelper.getSample(alias, team);
        final Assay assay = TestHelper.getAssay(alias, team, alias, alias);
        final AssayData assayData = TestHelper.getAssayData(alias, team, alias);

        final String filename = "missing_file_while_validation.fastq.gz";
        assayData.getFiles().get(0).setName(filename);

        submissionEnvelope.getStudies().add(study);
        submissionEnvelope.getSamples().add(sample);
        submissionEnvelope.getAssays().add(assay);
        submissionEnvelope.getAssayData().add(assayData);

        List<SingleValidationResult> singleValidationResultList =
                enaAssayDataValidator.validate(submissionEnvelope, assayData);
        final SingleValidationResult singleValidationResultBeforeFilter = singleValidationResultList.get(0);
        assertThat(singleValidationResultBeforeFilter.getValidationStatus(), is(SingleValidationResultStatus.Error));

        singleValidationResultList = enaAssayDataValidator.filterFileExistenceError(singleValidationResultList, assayData);

        final SingleValidationResult singleValidationResultAfterFilter = singleValidationResultList.get(0);
        assertThat(singleValidationResultAfterFilter.getValidationStatus(), is(SingleValidationResultStatus.Pass));
    }

    @Test
    public void test_validation_with_data(){
        AssayDataValidationMessageEnvelope envelope = createAssayDataValidationMessageEnvelope();

        enaAssayDataValidator.validateAssayData(envelope);

        SingleValidationResultsEnvelope expectedEnvelope = new SingleValidationResultsEnvelope(
                Collections.emptyList(),
                envelope.getValidationResultVersion(),
                envelope.getValidationResultUUID(),
                ValidationAuthor.Ena
        );

        ArgumentCaptor<SingleValidationResultsEnvelope> envelopeArgumentCaptor = ArgumentCaptor.forClass(SingleValidationResultsEnvelope.class);

        Mockito.verify(rabbitMessagingTemplate)
                .convertAndSend(
                        Mockito.eq(Exchanges.SUBMISSIONS),
                        Mockito.anyString(),
                        envelopeArgumentCaptor.capture()
                );

        SingleValidationResultsEnvelope actualEnvelope = envelopeArgumentCaptor.getValue();

        assertEquals(
                expectedEnvelope.getSingleValidationResults(),
                actualEnvelope.getSingleValidationResults()
        );

        assertEquals(
                expectedEnvelope.getValidationResultUUID(),
                actualEnvelope.getValidationResultUUID()
        );

        assertEquals(
                expectedEnvelope.getValidationResultVersion(),
                actualEnvelope.getValidationResultVersion()
        );

        assertEquals(
                expectedEnvelope.getValidationAuthor(),
                actualEnvelope.getValidationAuthor()
        );

    }

    private AssayDataValidationMessageEnvelope createAssayDataValidationMessageEnvelope() {
        final Team team = TestHelper.getTeam(CENTER_NAME);
        final String assayDataAlias = UUID.randomUUID().toString();
        final String assayAlias = UUID.randomUUID().toString();
        final String submissionId = UUID.randomUUID().toString();

        AssayData assayData = TestHelper.getAssayData(assayDataAlias,team,assayDataAlias);
        Assay assay = TestHelper.getAssay(assayAlias,team,"sample","study");
        Submittable<Assay> wrappedAssay = new Submittable<>(assay,submissionId);

        AssayDataValidationMessageEnvelope envelope = new AssayDataValidationMessageEnvelope();
        envelope.setEntityToValidate(assayData);
        envelope.getAssays().add(wrappedAssay);

        envelope.setSubmissionId(submissionId);
        envelope.setValidationResultUUID(UUID.randomUUID().toString());
        envelope.setValidationResultVersion(42);

        return  envelope;
    }

}
