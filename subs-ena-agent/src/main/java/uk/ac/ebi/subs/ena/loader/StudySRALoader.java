package uk.ac.ebi.subs.ena.loader;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.ac.ebi.ena.sra.StudyInfo;
import uk.ac.ebi.ena.sra.xml.StudyType;
import uk.ac.ebi.ena.sra.xml.SubmissionType;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.data.submittable.ENASubmittable;
import uk.ac.ebi.subs.ena.processor.SRALoaderAccessionException;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by neilg on 12/04/2017.
 */
@Service
public class StudySRALoader extends AbstractSRALoaderService<ENAStudy> {
    public static final String SCHEMA = "study";

    @Override
    public String executeSRALoader(String submissionXML, String submittableXML, Connection connection) throws Exception {
        String accession = null;
        try {
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
        } catch (Exception e) {
            throw new SRALoaderAccessionException(submissionXML,submittableXML);
        }
        if (accession == null ) {
            throw new SRALoaderAccessionException(submissionXML,submittableXML);
        }

        return accession;
    }

    @Override
    SubmissionType.ACTIONS createActions(SubmissionType submissionType, ENASubmittable enaSubmittable, String schema) {
        final SubmissionType.ACTIONS actions = super.createActions(submissionType, enaSubmittable, schema);
        final SubmissionType.ACTIONS.ACTION.HOLD hold = actions.addNewACTION().addNewHOLD();
        Calendar calendar = Calendar.getInstance();
        hold.setHoldUntilDate(calendar);
        if (enaSubmittable.getAccession() != null) {
            hold.setTarget(enaSubmittable.getAccession());
        }
        return actions;
    }

    String getSchema() {
        return "study";
    }

    @Override
    @Autowired
    @Qualifier("study")
    public void setMarshaller(Marshaller marshaller) {
        super.setMarshaller(marshaller);
    }
}
