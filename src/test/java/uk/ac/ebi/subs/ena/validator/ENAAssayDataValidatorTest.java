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
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.assertEnvelopesEqual;
import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.errorResult;
import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.expectedEnvelope;
import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.passResult;

/**
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

    private ArgumentCaptor<SingleValidationResultsEnvelope> envelopeArgumentCaptor;

    @Before
    public void setUp() {
        submissionEnvelope = new SubmissionEnvelope();
        submissionEnvelope.setSubmission(enaAssayDataValidator.createSubmission(SUBMISSION_ID));
        envelopeArgumentCaptor = ArgumentCaptor.forClass(SingleValidationResultsEnvelope.class);
    }

    private static final String CENTER_NAME = "test-team";

    @Test
    public void testExecuteSubmittableValidation() {
        final Team team = TestHelper.getTeam(CENTER_NAME);
        final String alias = UUID.randomUUID().toString();

        final Study study = TestHelper.getStudy(
                alias, team, "study_abstract", "Whole Genome Sequencing");
        final Sample sample = TestHelper.getSample(alias, team);
        final Assay assay = TestHelper.getAssay(alias, team, TestAccessions.BIOSAMPLE_ACCESSION, alias);
        final AssayData assayData = TestHelper.getAssayData(alias, team, alias);

        final String filename = "missing_file_while_validation.fastq.gz";
        assayData.getFiles().get(0).setName(filename);

        submissionEnvelope.getStudies().add(study);
        submissionEnvelope.getSamples().add(sample);
        submissionEnvelope.getAssays().add(assay);
        submissionEnvelope.getAssayData().add(assayData);

        List<SingleValidationResult> singleValidationResultList =
                enaAssayDataValidator.validate(submissionEnvelope, assayData);

        final SingleValidationResult singleValidationResultAfterFilter = singleValidationResultList.get(0);
        assertThat(singleValidationResultAfterFilter.getValidationStatus(), is(SingleValidationResultStatus.Pass));
    }

    @Test
    public void test_validation_with_good_data() {
        AssayDataValidationMessageEnvelope envelope = createAssayDataValidationMessageEnvelope();

        enaAssayDataValidator.validateAssayData(envelope);

        SingleValidationResultsEnvelope expectedEnvelope = expectedEnvelope(envelope, passResult(envelope));

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
    public void test_validation_with_no_file_ref() {
        AssayDataValidationMessageEnvelope envelope = createAssayDataValidationMessageEnvelope();

        envelope.getEntityToValidate().setFiles(Collections.emptyList()); // break the file

        SingleValidationResult missingFileGroup = errorResult(
                envelope,
                "In run, alias:\"" +
                        envelope.getEntityToValidate().getAlias() +
                        "@USI-test-team\", accession:\"\". Invalid group of files: . Supported file grouping(s) are: [ at least 1 \"CompleteGenomics_native\" files],[1 \"OxfordNanopore_native\" file],[ at least 1 \"PacBio_HDF5\" files],[1 \"bam\" file],[1 \"cram\" file],[1..2 \"fastq\" files],[1 \"sff\" file],[1 \"srf\" file]."

        );

        SingleValidationResult missingFileElement = errorResult(
                envelope,
                "Failed to validate run xml, error: Expected element 'FILE' before the end of the content in element FILES"
        );

        SingleValidationResultsEnvelope expectedEnvelope = expectedEnvelope(
                envelope,
                missingFileElement, missingFileGroup
        );

        enaAssayDataValidator.validateAssayData(envelope);

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
    public void test_validation_with_no_assay_ref() {
        AssayDataValidationMessageEnvelope envelope = createAssayDataValidationMessageEnvelope();

        envelope.getEntityToValidate().setAssayRefs(Collections.emptyList()); // break the assay

        SingleValidationResult missingExperimentRef1 = errorResult(
                envelope,
                "Failed to validate run xml, error: Expected element 'EXPERIMENT_REF' instead of 'DATA_BLOCK' here in element RUN"
        );

        SingleValidationResult missingExperimentRef2 = errorResult(
                envelope,
                "Failed to validate run xml, error: Expected element 'EXPERIMENT_REF' before the end of the content in element RUN"
        );

        SingleValidationResultsEnvelope expectedEnvelope = expectedEnvelope(
                envelope,
                missingExperimentRef1, missingExperimentRef2
        );

        enaAssayDataValidator.validateAssayData(envelope);

        Mockito.verify(rabbitMessagingTemplate, Mockito.atLeastOnce())
                .convertAndSend(
                        Mockito.eq(Exchanges.SUBMISSIONS),
                        Mockito.anyString(),
                        envelopeArgumentCaptor.capture()
                );

        SingleValidationResultsEnvelope actualEnvelope = envelopeArgumentCaptor.getValue();

        assertEnvelopesEqual(expectedEnvelope, actualEnvelope);
    }

    private AssayDataValidationMessageEnvelope createAssayDataValidationMessageEnvelope() {
        final Team team = TestHelper.getTeam(CENTER_NAME);
        final String assayDataAlias = UUID.randomUUID().toString();
        final String assayAlias = UUID.randomUUID().toString();
        final String sampleAlias = UUID.randomUUID().toString();
        final String submissionId = UUID.randomUUID().toString();

        AssayData assayData = TestHelper.getAssayData(assayDataAlias, team, assayDataAlias);
        Assay assay = TestHelper.getAssay(assayAlias, team, null, "study");
        assay.getSampleUses().get(0).getSampleRef().setAlias(sampleAlias);

        Submittable<Assay> wrappedAssay = new Submittable<>(assay, submissionId);

        AssayDataValidationMessageEnvelope envelope = new AssayDataValidationMessageEnvelope();
        envelope.setEntityToValidate(assayData);
        envelope.getAssays().add(wrappedAssay);

        envelope.setSubmissionId(submissionId);
        envelope.setValidationResultUUID(UUID.randomUUID().toString());
        envelope.setValidationResultVersion(42);

        return envelope;
    }

}
