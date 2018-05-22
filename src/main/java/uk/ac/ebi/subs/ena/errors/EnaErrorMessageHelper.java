package uk.ac.ebi.subs.ena.errors;

import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnaErrorMessageHelper {
    private final Pattern dataErrorRegex = Pattern.compile(
            "^In (\\w+), alias:\"(.+)@USI-(.*)\", accession:\"(.*)\". (.*)"
    );

    public boolean isDataError(SingleValidationResult validationResult){
        Matcher m = dataErrorRegex.matcher(validationResult.getMessage());
        return m.find();
    }

    public EnaDataErrorMessage parseDataError(SingleValidationResult validationResult) {
        Matcher m = dataErrorRegex.matcher(validationResult.getMessage());

        if (m.find()) {
            String enaEntityType = m.group(1);
            String alias = m.group(2);
            String teamName = m.group(3);
            String accession = m.group(4);
            String errorMessage = m.group(5);

            EnaDataErrorMessage enaDataErrorMessage = new EnaDataErrorMessage(
                    enaEntityType,
                    alias,
                    teamName,
                    accession,
                    errorMessage
            );
            return enaDataErrorMessage;
        }

        return null;
    }

    private final Pattern referenceErrorRegex = Pattern.compile(
            "^In reference:\"(.*)\", reference alias:\"(.+)@USI-(.*)\", reference accession:\"(.*)\". (.*)"
    );

    public boolean isReferenceError(SingleValidationResult validationResult){
        Matcher m = referenceErrorRegex.matcher(validationResult.getMessage());
        return m.find();
    }

    public EnaReferenceErrorMessage parseReferenceError(SingleValidationResult validationResult){
        Matcher m = referenceErrorRegex.matcher(validationResult.getMessage());

        if (m.find()){
            String referenceLocator = m.group(1);
            String referenceAlias = m.group(2);
            String referenceTeamName = m.group(3);
            String referenceAccession = m.group(4);
            String errorMessage = m.group(5);

            EnaReferenceErrorMessage enaReferenceErrorMessage = new EnaReferenceErrorMessage(
                    referenceLocator,
                    referenceAlias,
                    referenceTeamName,
                    referenceAccession,
                    errorMessage
            );

            return enaReferenceErrorMessage;
        }

        return null;
    }
}
