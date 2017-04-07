package uk.ac.ebi.subs.ena.processor;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.eclipse.persistence.annotations.TimeOfDay;
import org.springframework.oxm.Marshaller;
import org.w3c.dom.Document;

import uk.ac.ebi.ena.sra.SRALoader;
import uk.ac.ebi.ena.sra.StudyInfo;
import uk.ac.ebi.ena.sra.xml.SUBMISSIONSETDocument;
import uk.ac.ebi.ena.sra.xml.StudyType;
import uk.ac.ebi.ena.sra.xml.SubmissionType;
import uk.ac.ebi.subs.data.FullSubmission;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatus;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

//import uk.ac.ebi.ena.sra.SRALoader;
//import uk.ac.ebi.ena.sra.SRALoader;

public class ENAStudyProcessor extends ENAAgentProcessor<ENAStudy> {
    static String STUDY_SCHEMA = "study";
    String STUDY_TRANSFORMER = "/uk/ac/ebi/subs/xml/study_mapper.xslt";
    public static final String STUDY_SET_XSD = "https://github.com/enasequence/schema/blob/master/src/main/resources/uk/ac/ebi/ena/sra/schema/SRA.study.xsd";


    public ENAStudyProcessor(SubmissionEnvelope submissionEnvelope, Archive archive, Marshaller marshaller, Connection connection, String submissionAccountId, SRALoader.TransactionMode transactionMode) {
        super(submissionEnvelope, archive, marshaller, connection, submissionAccountId, transactionMode);
    }

    @Override
    Transformer getTransformer() throws URISyntaxException, TransformerConfigurationException {
        return getTransformer(STUDY_TRANSFORMER);
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
        //if (submittable.getReleaseDate() == null) throw new Exception("release date has not been set for study " + submittable.getId());
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

    @Override
    String getSchemaName() {
        return STUDY_SCHEMA;
    }



}
