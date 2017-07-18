package uk.ac.ebi.subs.ena.processor;

public class SRALoaderAccessionException extends Exception {
    private String submissionXML;
    private String submittableXML;

    public SRALoaderAccessionException(String submissionXML, String submittableXML, Throwable cause) {
        super("Unable to accession with submission XML " + submittableXML, cause);
        this.submissionXML = submissionXML;
        this.submittableXML = submittableXML;
    }

    public SRALoaderAccessionException(String submissionXML, String submittableXML) {
        super("Unable to accession with submission XML " + submittableXML);
    }

    public SRALoaderAccessionException(Throwable cause) {
        super(cause);
    }

    public String getSubmissionXML() {
        return submissionXML;
    }

    public String getSubmittableXML() {
        return submittableXML;
    }
}
