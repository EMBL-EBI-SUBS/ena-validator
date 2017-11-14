package uk.ac.ebi.subs.ena.loader;

import org.junit.After;
import org.junit.Before;
import uk.ac.ebi.ena.sra.xml.SUBMISSIONSETDocument;
import uk.ac.ebi.ena.sra.xml.SubmissionSetType;
import uk.ac.ebi.ena.sra.xml.SubmissionType;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.ena.helper.TestHelper;

import java.sql.Connection;
import java.util.List;

public abstract class AbstractSRALoaderTest {

    Connection connection = null;

    String centerName = "EBI";


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    protected String createSubmittable (String source, SubmissionType.ACTIONS.ACTION.ADD.Schema.Enum schema, String alias) {
        SUBMISSIONSETDocument submissionsetDocument = SUBMISSIONSETDocument.Factory.newInstance();
        final SubmissionSetType submissionSetType = submissionsetDocument.addNewSUBMISSIONSET();
        final SubmissionType submissionType = submissionSetType.addNewSUBMISSION();
        submissionType.setAlias(alias);
        submissionType.setCenterName(centerName);
        final SubmissionType.ACTIONS.ACTION.ADD add = submissionType.addNewACTIONS().addNewACTION().addNewADD();
        add.setSchema(schema);
        add.setSource(source);
        return submissionsetDocument.xmlText();
    }

    protected String createSubmittable (List<SubmissionType.ACTIONS.ACTION.ADD.Schema.Enum> schemaList, String alias) {
        SUBMISSIONSETDocument submissionsetDocument = SUBMISSIONSETDocument.Factory.newInstance();
        final SubmissionSetType submissionSetType = submissionsetDocument.addNewSUBMISSIONSET();
        final SubmissionType submissionType = submissionSetType.addNewSUBMISSION();
        submissionType.setAlias(alias);
        for (SubmissionType.ACTIONS.ACTION.ADD.Schema.Enum schema :schemaList) {
            final SubmissionType.ACTIONS.ACTION.ADD add = submissionType.addNewACTIONS().addNewACTION().addNewADD();
            add.setSchema(schema);
            add.setSource(schema.toString() + ".xml");
        }
        return submissionsetDocument.xmlText();
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    protected Team getTeam () {
        return TestHelper.getTeam(getCenterName());
    }
}
