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
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;

import java.util.UUID;

import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.assertEnvelopesEqual;
import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.errorResult;
import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.expectedEnvelope;
import static uk.ac.ebi.subs.ena.validator.ValidationResultUtil.passResult;

/**
 * Created by karoly on 09/06/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class ENAStudyValidatorTest {

    @Autowired
    ENAStudyValidator enaStudyValidator;

    @MockBean
    RabbitMessagingTemplate rabbitMessagingTemplate;

    private ArgumentCaptor<SingleValidationResultsEnvelope> envelopeArgumentCaptor;

    @Before
    public void setUp() {
        envelopeArgumentCaptor = ArgumentCaptor.forClass(SingleValidationResultsEnvelope.class);
    }

    private static final String CENTER_NAME = "test-team";

    @Test
    public void validate_good_study() {
        StudyValidationMessageEnvelope studyValidationMessageEnvelope = createStudyValidationMessageEnvelope();

        enaStudyValidator.validateStudy(studyValidationMessageEnvelope);

        SingleValidationResultsEnvelope expectedEnvelope = expectedEnvelope(
                studyValidationMessageEnvelope,
                passResult(studyValidationMessageEnvelope)
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
    public void validate_study_no_study_abstract() {
        StudyValidationMessageEnvelope studyValidationMessageEnvelope = createStudyValidationMessageEnvelope();
        Study study = studyValidationMessageEnvelope.getEntityToValidate();

        study.getAttributes().remove("study_abstract");


        enaStudyValidator.validateStudy(studyValidationMessageEnvelope);

        SingleValidationResultsEnvelope expectedEnvelope = expectedEnvelope(
                studyValidationMessageEnvelope,
                errorResult(studyValidationMessageEnvelope, "Value for attribute study_abstract is required.")
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
    public void validate_study_no_study_type() {
        StudyValidationMessageEnvelope studyValidationMessageEnvelope = createStudyValidationMessageEnvelope();
        Study study = studyValidationMessageEnvelope.getEntityToValidate();

        study.getAttributes().remove("study_type");


        enaStudyValidator.validateStudy(studyValidationMessageEnvelope);

        SingleValidationResultsEnvelope expectedEnvelope = expectedEnvelope(
                studyValidationMessageEnvelope,
                errorResult(studyValidationMessageEnvelope, "Value for attribute study_type is required.")
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


    private StudyValidationMessageEnvelope createStudyValidationMessageEnvelope() {
        Team team = TestHelper.getTeam(CENTER_NAME);
        String studyAlias = UUID.randomUUID().toString();
        String submissionId = UUID.randomUUID().toString();

        Study study = TestHelper.getStudy(studyAlias, team, "study_abstract", "Whole Genome Sequencing");


        StudyValidationMessageEnvelope envelope = new StudyValidationMessageEnvelope();
        envelope.setEntityToValidate(study);

        envelope.setSubmissionId(submissionId);
        envelope.setValidationResultUUID(UUID.randomUUID().toString());
        envelope.setValidationResultVersion(42);

        return envelope;
    }

}