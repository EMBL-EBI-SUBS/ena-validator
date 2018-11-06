package uk.ac.ebi.subs.ena.errors;

import java.util.Objects;

/**
 * This class represents an error message coming from the ENA archive.
 */
public class EnaDataErrorMessage {

    private final String enaEntityType;
    private final String alias;
    private final String teamName;
    private final String accession;
    private final String message;

    public EnaDataErrorMessage(String enaEntityType, String alias, String teamName, String accession, String message) {
        this.enaEntityType = enaEntityType;
        this.alias = alias;
        this.teamName = teamName;
        this.accession = accession;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnaDataErrorMessage that = (EnaDataErrorMessage) o;
        return Objects.equals(enaEntityType, that.enaEntityType) &&
                Objects.equals(alias, that.alias) &&
                Objects.equals(teamName, that.teamName) &&
                Objects.equals(accession, that.accession) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {

        return Objects.hash(enaEntityType, alias, teamName, accession, message);
    }

    @Override
    public String toString() {
        return "EnaDataErrorMessage{" +
                "enaEntityType='" + enaEntityType + '\'' +
                ", alias='" + alias + '\'' +
                ", teamName='" + teamName + '\'' +
                ", accession='" + accession + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public String getEnaEntityType() {
        return enaEntityType;
    }

    public String getAlias() {
        return alias;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getAccession() {
        return accession;
    }

    public String getMessage() {
        return message;
    }
}
