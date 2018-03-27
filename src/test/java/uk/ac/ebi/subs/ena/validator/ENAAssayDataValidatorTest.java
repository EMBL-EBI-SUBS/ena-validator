package uk.ac.ebi.subs.ena.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.helper.TestHelper;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 * Created by karoly on 09/06/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class ENAAssayDataValidatorTest {

    @Autowired
    ENAAssayDataValidator enaAssayDataValidator;

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

}
