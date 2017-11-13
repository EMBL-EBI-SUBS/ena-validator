package uk.ac.ebi.subs.ena.processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by neilg on 18/05/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class ENAStudyProcessorTest {

    @Autowired
    ENAStudyProcessor enaStudyProcessor;

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void process () throws Exception {
        String alias = UUID.randomUUID().toString();
        final Team team = TestHelper.getTeam("test-team");
        process(enaStudyProcessor,alias,team);
    }

    static void process(ENAStudyProcessor enaStudyProcessor, String alias, Team team) throws Exception {
        final ENAStudy enaStudy = TestHelper.getENAStudy(alias, team);
        final ProcessingCertificate processingCertificate = enaStudyProcessor.process(enaStudy);
        assertThat("study accessioned", enaStudy.getAccession(), startsWith("ERP"));
        assertThat("correct certificate",processingCertificate.getProcessingStatus() , equalTo(ProcessingStatusEnum.Received));
    }

    @Test
    public void processSubmission() throws Exception {
        String alias = UUID.randomUUID().toString();
        final Team team = TestHelper.getTeam("test-team");
        final Study study = TestHelper.getStudy(alias, team,"study_abstract", "Whole Genome Sequencing");
        final LocalDate date = LocalDate.parse("2018-12-25");
        study.setReleaseDate(date);
        final ProcessingStatusEnum processingStatusEnum = ProcessingStatusEnum.Received;
        processSubmission(study, processingStatusEnum);
    }

    @Test
    public void processDuplicateSubmission() throws Exception {
        String alias = UUID.randomUUID().toString();
        final Team team = TestHelper.getTeam("test-team");
        final Study study = TestHelper.getStudy(alias, team,"study_abstract", "Whole Genome Sequencing");
        final LocalDate date = LocalDate.parse("2018-12-25");
        study.setReleaseDate(date);
        final ProcessingStatusEnum processingStatusEnum = ProcessingStatusEnum.Received;
        processSubmission(study, processingStatusEnum);
        final ProcessingStatusEnum processingStatusEnum2 = ProcessingStatusEnum.Error;
        final Study study2 = TestHelper.getStudy(alias, team,"study_abstract", "Whole Genome Sequencing");
        final LocalDate date2 = LocalDate.parse("2018-12-25");
        study2.setReleaseDate(date2);
        study2.setId(UUID.randomUUID().toString());
        final ArrayList<SingleValidationResult> singleValidationResultList = new ArrayList<>();
        final ProcessingCertificate processingCertificate = enaStudyProcessor.processAndConvertSubmittable(study2, singleValidationResultList);
        assertThat(processingCertificate, is(equalTo(new ProcessingCertificate(study2, Archive.Ena, processingStatusEnum2, null))));
    }

    private void processSubmission(Study study, ProcessingStatusEnum processingStatusEnum) {
        study.setId(UUID.randomUUID().toString());
        final ArrayList<SingleValidationResult> singleValidationResultList = new ArrayList<>();
        final ProcessingCertificate processingCertificate = enaStudyProcessor.processAndConvertSubmittable(study, singleValidationResultList);
        assertThat(processingCertificate, is(equalTo(new ProcessingCertificate(study, Archive.Ena, processingStatusEnum, study.getAccession()))));
    }

    @Test
    public void processInvalidStudyTypeSubmission() throws Exception {
        String alias = UUID.randomUUID().toString();
        final Team team = TestHelper.getTeam("test-team");
        final Study study = TestHelper.getStudy(alias, team,"study_abstract", "Blah");
        final LocalDate date = LocalDate.parse("2018-12-25");
        study.setReleaseDate(date);
        study.setId(UUID.randomUUID().toString());
        final ArrayList<SingleValidationResult> singleValidationResultList = new ArrayList<>();
        final ProcessingCertificate processingCertificate = enaStudyProcessor.processAndConvertSubmittable(study, singleValidationResultList);
        assertThat(processingCertificate, is(equalTo(new ProcessingCertificate(study, Archive.Ena, ProcessingStatusEnum.Error, null))));
    }

}