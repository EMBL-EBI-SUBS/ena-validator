package uk.ac.ebi.subs.ena;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * Created by neilg on 25/05/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class EnaAgentSubmissionsProcessorTest {

    @Autowired
    EnaAgentSubmissionsProcessor enaAgentSubmissionsProcessor;

    @Test
    public void submissionTest() throws Exception {
        String alias = UUID.randomUUID().toString();
        final Team team = TestHelper.getTeam("test-team");
        final Study study = TestHelper.getStudy(alias, team,"study_abstract","Whole Genome Sequencing");
        study.setId(UUID.randomUUID().toString());
        uk.ac.ebi.subs.data.Submission submission = new uk.ac.ebi.subs.data.Submission();
        submission.setTeam(team);
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope(submission);
        submissionEnvelope.getStudies().add(study);
        final Sample sample = TestHelper.getSample(alias, team);
        submissionEnvelope.getSamples().add(sample);
        final Assay assay = TestHelper.getAssay(alias,team,alias,alias);
        submissionEnvelope.getAssays().add(assay);
        final ProcessingCertificateEnvelope processingCertificateEnvelope = enaAgentSubmissionsProcessor.processSubmission(submissionEnvelope);
        ProcessingCertificate studyProcessingCertificate = new ProcessingCertificate(study, Archive.Ena, ProcessingStatusEnum.Received, study.getAccession());
        ProcessingCertificate sampleProcessingCertificate = new ProcessingCertificate(sample, Archive.Ena, ProcessingStatusEnum.Received, sample.getAccession());
        ProcessingCertificate assayProcessingCertificate = new ProcessingCertificate(assay, Archive.Ena, ProcessingStatusEnum.Received, assay.getAccession());
        assertThat("correct study certs",
                processingCertificateEnvelope.getProcessingCertificates(),
                containsInAnyOrder(
                        studyProcessingCertificate, sampleProcessingCertificate,assayProcessingCertificate
                )

        );

    }

    @Test
    public void studyUpdateTest() throws Exception {
        String alias = UUID.randomUUID().toString();
        final Team team = TestHelper.getTeam("test-team");
        final Study study = TestHelper.getStudy(alias, team,"study_abstract","Whole Genome Sequencing");
        study.setId(UUID.randomUUID().toString());
        uk.ac.ebi.subs.data.Submission submission = new uk.ac.ebi.subs.data.Submission();
        submission.setTeam(team);
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope(submission);
        submissionEnvelope.getStudies().add(study);
        final ProcessingCertificateEnvelope processingCertificateEnvelope = enaAgentSubmissionsProcessor.processSubmission(submissionEnvelope);
        ProcessingCertificate studyProcessingCertificate = new ProcessingCertificate(study, Archive.Ena, ProcessingStatusEnum.Received, study.getAccession());
        assertThat("correct study certs",
                processingCertificateEnvelope.getProcessingCertificates(),
                containsInAnyOrder(
                        studyProcessingCertificate
                )

        );

        final Study study2 = TestHelper.getStudy(alias, team,"study_abstract","Whole Genome Sequencing");
        study2.setAccession(study.getAccession());
        final Attribute attribute = new Attribute();
        attribute.setName("Test Update Attribute");
        attribute.setValue("Test Update Value");
        study2.getAttributes().add(attribute);
        study2.setId(UUID.randomUUID().toString());

        uk.ac.ebi.subs.data.Submission updateSubmission = new uk.ac.ebi.subs.data.Submission();
        updateSubmission.setTeam(team);
        SubmissionEnvelope updateSubmissionEnvelope = new SubmissionEnvelope(updateSubmission);
        updateSubmissionEnvelope.getStudies().add(study2);
        final ProcessingCertificateEnvelope updateProcessingCertificateEnvelope = enaAgentSubmissionsProcessor.processSubmission(updateSubmissionEnvelope);
        ProcessingCertificate studyUpdateProcessingCertificate =
                new ProcessingCertificate(study2, Archive.Ena, ProcessingStatusEnum.Received, study.getAccession());
        assertThat("correct study certs",
                updateProcessingCertificateEnvelope.getProcessingCertificates(),
                containsInAnyOrder(
                        studyUpdateProcessingCertificate
                )

        );
    }

}