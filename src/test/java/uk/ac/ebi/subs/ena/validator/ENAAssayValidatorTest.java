package uk.ac.ebi.subs.ena.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.component.StudyRef;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.validator.data.AssayValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.UUID;

import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.assertEnvelopesEqual;
import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.errorResult;
import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.expectedEnvelope;
import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.passResult;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class ENAAssayValidatorTest {

    @Autowired
    ENAAssayValidator enaAssayValidator;

    @MockBean
    RabbitMessagingTemplate rabbitMessagingTemplate;

    private ArgumentCaptor<SingleValidationResultsEnvelope> envelopeArgumentCaptor;

    @Before
    public void setUp() {
        envelopeArgumentCaptor = ArgumentCaptor.forClass(SingleValidationResultsEnvelope.class);
    }

    private static final String CENTER_NAME = "test-team";

    @Test
    public void validate_good_assay() {
        AssayValidationMessageEnvelope assayValidationMessageEnvelope = createAssayValidationMessageEnvelope();

        enaAssayValidator.validateAssay(assayValidationMessageEnvelope);

        SingleValidationResultsEnvelope expectedEnvelope = expectedEnvelope(
                assayValidationMessageEnvelope,
                passResult(assayValidationMessageEnvelope)
        );

        Mockito.verify(rabbitMessagingTemplate)
                .convertAndSend(
                        Mockito.eq(Exchanges.SUBMISSIONS),
                        Mockito.anyString(),
                        envelopeArgumentCaptor.capture()
                );

        SingleValidationResultsEnvelope actualEnvelope = envelopeArgumentCaptor.getValue();

        assertEnvelopesEqual(expectedEnvelope, actualEnvelope);
    }

    @Test
    public void validate_assay_no_study() {
        AssayValidationMessageEnvelope assayValidationMessageEnvelope = createAssayValidationMessageEnvelope();
        Assay assay = assayValidationMessageEnvelope.getEntityToValidate();

        assay.setStudyRef(new StudyRef());

        enaAssayValidator.validateAssay(assayValidationMessageEnvelope);

        SingleValidationResultsEnvelope expectedEnvelope = expectedEnvelope(
                assayValidationMessageEnvelope,
                errorResult(assayValidationMessageEnvelope,
                        "In experiment, alias:\""+assay.getAlias()+"@USI-test-team\", accession:\"\", In reference:\"STUDY_REF\", reference alias:\"\", reference accession:\"\"."
                        + " Failed to find referenced study, accession \"\"."
                        )
        );

        Mockito.verify(rabbitMessagingTemplate)
                .convertAndSend(
                        Mockito.eq(Exchanges.SUBMISSIONS),
                        Mockito.anyString(),
                        envelopeArgumentCaptor.capture()
                );

        SingleValidationResultsEnvelope actualEnvelope = envelopeArgumentCaptor.getValue();

        assertEnvelopesEqual(expectedEnvelope, actualEnvelope);
    }

    @Test
    public void validate_study_no_sample() {
        AssayValidationMessageEnvelope assayValidationMessageEnvelope = createAssayValidationMessageEnvelope();
        Assay assay = assayValidationMessageEnvelope.getEntityToValidate();

        assay.getSampleUses().clear();


        enaAssayValidator.validateAssay(assayValidationMessageEnvelope);

        SingleValidationResultsEnvelope expectedEnvelope = expectedEnvelope(
                assayValidationMessageEnvelope,
                errorResult(assayValidationMessageEnvelope, "Failed to validate experiment xml, error: Expected element 'SAMPLE_DESCRIPTOR' instead of 'LIBRARY_DESCRIPTOR' here in element DESIGN"),
                errorResult(assayValidationMessageEnvelope, "Failed to validate experiment xml, error: Expected element 'SAMPLE_DESCRIPTOR' before the end of the content in element DESIGN")
        );

        Mockito.verify(rabbitMessagingTemplate)
                .convertAndSend(
                        Mockito.eq(Exchanges.SUBMISSIONS),
                        Mockito.anyString(),
                        envelopeArgumentCaptor.capture()
                );

        SingleValidationResultsEnvelope actualEnvelope = envelopeArgumentCaptor.getValue();

        assertEnvelopesEqual(expectedEnvelope, actualEnvelope);
    }


    private AssayValidationMessageEnvelope createAssayValidationMessageEnvelope() {
        Team team = TestHelper.getTeam(CENTER_NAME);
        String assayAlias = UUID.randomUUID().toString();
        String studyAlias = UUID.randomUUID().toString();
        String submissionId = UUID.randomUUID().toString();
        String sampleAlias = UUID.randomUUID().toString();

        Assay assay = TestHelper.getAssay(assayAlias, team, null, studyAlias);
        assay.getSampleUses().get(0).getSampleRef().setAlias(sampleAlias);

        Study study = TestHelper.getStudy(studyAlias, team, "study_abstract", "Whole Genome Sequencing");

        AssayValidationMessageEnvelope envelope = new AssayValidationMessageEnvelope();
        envelope.setEntityToValidate(assay);

        envelope.setStudy(new Submittable<Study>(study,submissionId));
        envelope.setSubmissionId(submissionId);
        envelope.setValidationResultUUID(UUID.randomUUID().toString());
        envelope.setValidationResultVersion(42);

        return envelope;
    }

}