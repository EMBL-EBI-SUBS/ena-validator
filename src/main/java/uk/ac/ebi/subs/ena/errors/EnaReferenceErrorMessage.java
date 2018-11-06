package uk.ac.ebi.subs.ena.errors;

import java.util.Objects;

/**
 * This class represents an error message data object dealing with ENA related reference errors.
 */
public class EnaReferenceErrorMessage {

    private final String referenceLocator;
    private final String referenceAlias;
    private final String referenceTeamName;
    private final String referenceAccession;
    private final String errorMessage;

    public EnaReferenceErrorMessage(String referenceLocator, String referenceAlias, String referenceTeamName, String referenceAccession, String errorMessage) {
        this.referenceLocator = referenceLocator;
        this.referenceAlias = referenceAlias;
        this.referenceTeamName = referenceTeamName;
        this.referenceAccession = referenceAccession;
        this.errorMessage = errorMessage;
    }

    public String getReferenceLocator() {
        return referenceLocator;
    }

    public String getReferenceAlias() {
        return referenceAlias;
    }

    public String getReferenceTeamName() {
        return referenceTeamName;
    }

    public String getReferenceAccession() {
        return referenceAccession;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnaReferenceErrorMessage that = (EnaReferenceErrorMessage) o;
        return Objects.equals(referenceLocator, that.referenceLocator) &&
                Objects.equals(referenceAlias, that.referenceAlias) &&
                Objects.equals(referenceTeamName, that.referenceTeamName) &&
                Objects.equals(referenceAccession, that.referenceAccession) &&
                Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {

        return Objects.hash(referenceLocator, referenceAlias, referenceTeamName, referenceAccession, errorMessage);
    }

    @Override
    public String toString() {
        return "EnaReferenceErrorMessage{" +
                "referenceLocator='" + referenceLocator + '\'' +
                ", referenceAlias='" + referenceAlias + '\'' +
                ", referenceTeamName='" + referenceTeamName + '\'' +
                ", referenceAccession='" + referenceAccession + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
