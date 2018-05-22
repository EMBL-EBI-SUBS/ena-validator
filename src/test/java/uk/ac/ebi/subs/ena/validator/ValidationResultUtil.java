package uk.ac.ebi.subs.ena.validator;

import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ValidationResultUtil {

    public static SingleValidationResult passResult(ValidationMessageEnvelope envelope) {
        SingleValidationResult result = new SingleValidationResult(
                ValidationAuthor.Ena,
                envelope.getEntityToValidate().getId()
        );

        result.setValidationStatus(SingleValidationResultStatus.Pass);
        return result;
    }

    public static SingleValidationResult errorResult(ValidationMessageEnvelope envelope, String message) {
        SingleValidationResult result = new SingleValidationResult(
                ValidationAuthor.Ena,
                envelope.getEntityToValidate().getId()
        );

        result.setValidationStatus(SingleValidationResultStatus.Error);

        result.setMessage(message);

        return result;
    }

    public static SingleValidationResultsEnvelope expectedEnvelope(ValidationMessageEnvelope envelope, SingleValidationResult... expectedResults) {
        return new SingleValidationResultsEnvelope(
                Arrays.asList(expectedResults),
                envelope.getValidationResultVersion(),
                envelope.getValidationResultUUID(),
                ValidationAuthor.Ena
        );
    }

    public static void assertEnvelopesEqual(SingleValidationResultsEnvelope expectedEnvelope, SingleValidationResultsEnvelope actualEnvelope) {
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
}
