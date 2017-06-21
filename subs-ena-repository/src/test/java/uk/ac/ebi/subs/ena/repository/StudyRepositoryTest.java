package uk.ac.ebi.subs.ena.repository;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import uk.ac.ebi.ena.sra.xml.STUDYSETDocument;
import uk.ac.ebi.ena.sra.xml.StudySetType;
import uk.ac.ebi.ena.sra.xml.StudyType;
import uk.ac.ebi.subs.ena.ENATestRepositoryApplication;
import uk.ac.ebi.subs.ena.data.Study;
import uk.ac.ebi.subs.ena.data.SubmissionStatus;

/**
 * Created by neilg on 25/04/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ENATestRepositoryApplication.class)
public class StudyRepositoryTest extends SubmittableSRARepositoryTest<Study,StudyRepository> {


    @Override
    public String getId() {
        return "ERP005799";
    }

    @Override
    public String getAlias() {
        return "Salmonella_Typhi_HTS";
    }

    @Override
    public Study createSubmittable(String alias, String submissionAccountId, SubmissionStatus submissionStatus) {
        STUDYSETDocument studysetDocument = STUDYSETDocument.Factory.newInstance();
        final StudySetType studySetType = studysetDocument.addNewSTUDYSET();
        final StudyType studyType = studySetType.addNewSTUDY();
        studyType.setAlias(alias);
        Document studyDocument = (Document) studysetDocument.getDomNode();
        Study study = new Study();
        study.setSubmissionAccountId(submissionAccountId);
        study.setSubmissionStatus(submissionStatus);
        study.setDocument(studyDocument);
        study.updateMD5();
        return study;
    }

    @Autowired
    void setSubmissionRepository(StudyRepository repository) {
        this.repository = repository;
    }
}