package uk.ac.ebi.subs.ena.processor;

import org.springframework.oxm.Marshaller;
import uk.ac.ebi.ena.sra.SRALoader;
import uk.ac.ebi.ena.sra.StudyInfo;
import uk.ac.ebi.ena.sra.xml.StudyType;
import uk.ac.ebi.ena.sra.xml.SubmissionType;
import uk.ac.ebi.subs.data.status.ProcessingStatus;
import uk.ac.ebi.subs.data.submittable.BaseSubmittable;
import uk.ac.ebi.subs.data.submittable.BaseSubmittableFactory;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.sql.Connection;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

//import uk.ac.ebi.ena.sra.SRALoader;
//import uk.ac.ebi.ena.sra.SRALoader;

public class ENAStudyProcessor extends ENAAgentSubmittableProcessor<ENAStudy> {
    static String STUDY_SCHEMA = "study";
    String STUDY_TRANSFORMER = "/uk/ac/ebi/subs/xml/study_mapper.xslt";
    public static final String STUDY_SET_XSD = "https://github.com/enasequence/schema/blob/master/src/main/resources/uk/ac/ebi/ena/sra/schema/SRA.study.xsd";


    public ENAStudyProcessor(SubmissionEnvelope submissionEnvelope, Marshaller marshaller, Connection connection, String submissionAccountId, SRALoader.TransactionMode transactionMode) {
        super(submissionEnvelope, marshaller, connection, submissionAccountId, transactionMode);
    }

    @Override
    protected void addActions(SubmissionType submissionType, ENAStudy submittable) {
        super.addActions(submissionType, submittable);
        final SubmissionType.ACTIONS.ACTION.HOLD hold = submissionType.getACTIONS().addNewACTION().addNewHOLD();
        Calendar calendar = Calendar.getInstance();
        // add release date
        hold.setHoldUntilDate(calendar);
        if (submittable.getAccession() != null) {
            hold.setTarget(submittable.getAccession());
        }
    }

    @Override
    protected ProcessingStatus loadData(ENAStudy submittable, SubmissionEnvelope submissionEnvelope) throws Exception {
        if (submittable.getBaseObject().getReleaseDate() == null) throw new Exception("release date has not been set for study " + submittable.getId());
        return super.loadData(submittable, submissionEnvelope);
    }

    @Override
    String executeSRALoader (String submissionXML, String submittableXML) throws Exception {
        String accession = null;
        if (sraLoader.eraputRestWebin(submissionXML,
                submittableXML, null, null, null, null, null, null, null, null,
                null, authResult, null, connection) == 0) {
            final Map<StudyType, StudyInfo> studys = sraLoader.getStudys();
            if (studys != null) {
                if (studys.values().iterator().hasNext()) {
                    accession = studys.values().iterator().next().getStudyAccession();
                    logger.info("Created ENA study with accession " + accession);
                }
            }
        } else {
            logValidationErrors();
        }
        if (accession == null ) {
            throw new SRALoaderAccessionException(submissionXML,submittableXML);
        }
        return accession;
    }

    @Override
    protected Class<? extends BaseSubmittableFactory> getBaseSubmittableClass() {
        return ENAStudy.class;
    }

    @Override
    protected List<? extends BaseSubmittable> getBaseSubmittables(SubmissionEnvelope submissionEnvelope) {
        return submissionEnvelope.getStudies();
    }

    /*
    @Override
    List<ENAStudy> getSubmittables(FullSubmission fullSubmission) {
        List <ENAStudy> enaStudies = new ArrayList<>();
        for (Study study : fullSubmission.getStudies()) {
            try {
                enaStudies.add(new ENAStudy(study));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return enaStudies;
    }
    */

    @Override
    String getSchemaName() {
        return STUDY_SCHEMA;
    }

}
