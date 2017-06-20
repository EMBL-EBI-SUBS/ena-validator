package uk.ac.ebi.subs.ena.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.ebi.ena.sra.xml.SUBMISSIONSETDocument;
import uk.ac.ebi.ena.sra.xml.SubmissionSetType;
import uk.ac.ebi.ena.sra.xml.SubmissionType;
import uk.ac.ebi.subs.ena.data.Submission;
import uk.ac.ebi.subs.ena.data.SubmissionStatus;


/**
 * Created by neilg on 26/04/2017.
 */

public class SubmissionRepositoryTest extends SRAInfoRepositoryTest<Submission,SubmissionRepository> {

    @Override
    public String getId() {
        return "ERA000001";
    }

    @Override
    public String getAlias() {
        return "Katryn Holt";
    }

    @Override
    public Submission createSubmittable(String alias, String submissionAccountId, SubmissionStatus submissionStatus) {
        SUBMISSIONSETDocument submissionsetDocument = SUBMISSIONSETDocument.Factory.newInstance();
        final SubmissionSetType submissionSetType = submissionsetDocument.addNewSUBMISSIONSET();
        final SubmissionType submissionType = submissionSetType.addNewSUBMISSION();
        submissionType.setAlias(alias);
        submissionType.setCenterName("SC");
        Submission submission = new Submission();
        submission.setSubmissionAccountId(submissionAccountId);
        submission.setDocument((Document)submissionsetDocument.getDomNode());

        Document document = documentBuilder.newDocument();
        final Element element = (Element)document.importNode(((Document) submissionsetDocument.getDomNode()).getDocumentElement(), true);
        document.appendChild(element);
        submission.setDocument(document);
        submission.updateMD5();
        return submission;
    }

    @Autowired
    void setSubmissionRepository(SubmissionRepository repository) {
        this.repository = repository;
    }
}
