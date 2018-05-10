package errors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.ena.errors.EnaDataErrorMessage;
import uk.ac.ebi.subs.ena.errors.EnaErrorMessageHelper;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;


public class EnaErrorMessageHelperTest {

//    "Failed to validate run xml, error: Expected element 'EXPERIMENT_REF' instead of 'DATA_BLOCK' here in element RUN";
  //          "Failed to validate run xml, error: Expected element 'EXPERIMENT_REF' before the end of the content in element RUN"

    private static final String EXPERIMENT_DATA_ERROR = "In experiment, alias:\"13afaffd-4664-486e-a4eb-ee663a149754@USI-test-team\", accession:\"\". Missing study reference from experiment";
    private static final String EXPERIMENT_REFERENCE_ERROR = "In reference:\"SAMPLE_DESCRIPTOR\", reference alias:\"sample@USI-test-team\", reference accession:\"\". Failed to find referenced sample, alias sample@USI-test-team.";
    private static final String NULL_SAMPLE_IN_EXPERIMENT_ERROR = "Sample in experiment is null";
    private static final String RUN_FILE_ERROR = "In run, alias:\"96e0e28b-7dcf-47c2-9a16-530d001133ed@USI-test-team\", accession:\"\". File \"Test.fastq.gz\" dont exist in the upload area.";

    private EnaErrorMessageHelper enaErrorMessageHelper = new EnaErrorMessageHelper();

    private SingleValidationResult experimentDataError;
    private SingleValidationResult experimentReferenceError;
    private SingleValidationResult nullSampleInExperimentError;
    private SingleValidationResult runFileError;

    @Before
    public void buildUp() {
        enaErrorMessageHelper = new EnaErrorMessageHelper();

        experimentDataError = validationResult(EXPERIMENT_DATA_ERROR);
        experimentReferenceError = validationResult(EXPERIMENT_REFERENCE_ERROR);
        nullSampleInExperimentError = validationResult(NULL_SAMPLE_IN_EXPERIMENT_ERROR);
        runFileError = validationResult(RUN_FILE_ERROR);
    }

    @Test
    public void identify_data_errors() {
        Assert.assertTrue(
                enaErrorMessageHelper.isDataError(experimentDataError)
        );
        Assert.assertFalse(
                enaErrorMessageHelper.isDataError(experimentReferenceError)
        );
        Assert.assertFalse(
                enaErrorMessageHelper.isDataError(nullSampleInExperimentError)
        );
        Assert.assertTrue(
                enaErrorMessageHelper.isDataError(runFileError)
        );
    }

    @Test
    public void identify_reference_errors() {
        Assert.assertFalse(
                enaErrorMessageHelper.isReferenceError(experimentDataError)
        );
        Assert.assertTrue(
                enaErrorMessageHelper.isReferenceError(experimentReferenceError)
        );
        Assert.assertFalse(
                enaErrorMessageHelper.isReferenceError(nullSampleInExperimentError)
        );
        Assert.assertFalse(
                enaErrorMessageHelper.isReferenceError(runFileError)
        );
    }

    @Test
    public void parse_data_error() {
        EnaDataErrorMessage actualErrorMessage = enaErrorMessageHelper.parseDataError(experimentDataError);

        EnaDataErrorMessage expectedErrorMessage = new EnaDataErrorMessage(
                "experiment",
                "13afaffd-4664-486e-a4eb-ee663a149754",
                "test-team",
                "",
                "Missing study reference from experiment"
        );

        Assert.assertEquals(expectedErrorMessage,actualErrorMessage);
    }

    @Test
    public void parse_file_error() {
        EnaDataErrorMessage actualErrorMessage = enaErrorMessageHelper.parseDataError(runFileError);

        EnaDataErrorMessage expectedErrorMessage = new EnaDataErrorMessage(
                "run",
                "96e0e28b-7dcf-47c2-9a16-530d001133ed",
                "test-team",
                "",
                "File \"Test.fastq.gz\" dont exist in the upload area."
        );

        Assert.assertEquals(expectedErrorMessage,actualErrorMessage);
    }

    private SingleValidationResult validationResult(String message) {
        SingleValidationResult validationResult = new SingleValidationResult();
        validationResult.setMessage(message);
        return validationResult;
    }
}
