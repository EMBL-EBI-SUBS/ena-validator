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
import uk.ac.ebi.subs.data.submittable.Analysis;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.validator.data.AnalysisValidationEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.UUID;

import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.assertEnvelopesEqual;
import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.errorResult;
import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.expectedEnvelope;
import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.passResult;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class EnaAnalysisValidatorTest {

    @Autowired
    ENAAnalysisValidator enaAnalysisValidator;

    @MockBean
    RabbitMessagingTemplate rabbitMessagingTemplate;

    private ArgumentCaptor<SingleValidationResultsEnvelope> envelopeArgumentCaptor;

    @Before
    public void setUp() {
        envelopeArgumentCaptor = ArgumentCaptor.forClass(SingleValidationResultsEnvelope.class);
    }

    private static final String CENTER_NAME = "test-team";

    @Test
    public void test_good_analysis() {
        AnalysisValidationEnvelope envelope = createAnalysisEnvelope();

        enaAnalysisValidator.validateAnalysis(envelope);

        SingleValidationResultsEnvelope expectedEnvelope = expectedEnvelope(
                envelope,
                passResult(envelope)
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
    public void test_validation_with_no_study_ref() {
        AnalysisValidationEnvelope envelope = createAnalysisEnvelope();

        envelope.getEntityToValidate().getStudyRefs().clear();

        SingleValidationResult missingStudyRef = errorResult(
                envelope,
                "In analysis, alias:\"" + envelope.getEntityToValidate().getAlias()
                        + "@USI-test-team\", accession:\"\". Invalid number of study references: 0. Supported study references for SEQUENCE_VARIATION analysis type are: 1"
        );

        SingleValidationResultsEnvelope expectedEnvelope = expectedEnvelope(
                envelope,
                missingStudyRef
        );

        enaAnalysisValidator.validateAnalysis(envelope);

        Mockito.verify(rabbitMessagingTemplate)
                .convertAndSend(
                        Mockito.eq(Exchanges.SUBMISSIONS),
                        Mockito.anyString(),
                        envelopeArgumentCaptor.capture()
                );

        SingleValidationResultsEnvelope actualEnvelope = envelopeArgumentCaptor.getValue();

        assertEnvelopesEqual(expectedEnvelope, actualEnvelope);
    }

    private AnalysisValidationEnvelope createAnalysisEnvelope() {
        Team team = TestHelper.getTeam(CENTER_NAME);
        String analysisAlias = UUID.randomUUID().toString();
        String studyAlias = UUID.randomUUID().toString();
        String submissionId = UUID.randomUUID().toString();
        String sampleAlias = UUID.randomUUID().toString();

        Analysis analysis = TestHelper.getSeqVarAnalysis(analysisAlias, team, null, studyAlias);
        analysis.getSampleRefs().get(0).setAlias(sampleAlias);

        Study study = TestHelper.getStudy(studyAlias, team, "study_abstract", "Whole Genome Sequencing");
        Sample sample = TestHelper.getSample(sampleAlias, team);

        AnalysisValidationEnvelope envelope = new AnalysisValidationEnvelope();
        envelope.setEntityToValidate(analysis);

        envelope.getStudies().add(new Submittable<Study>(study, submissionId));
        envelope.getSamples().add(new Submittable<Sample>(sample, submissionId));
        envelope.setSubmissionId(submissionId);
        envelope.setValidationResultUUID(UUID.randomUUID().toString());
        envelope.setValidationResultVersion(7);

        return envelope;
    }
}
