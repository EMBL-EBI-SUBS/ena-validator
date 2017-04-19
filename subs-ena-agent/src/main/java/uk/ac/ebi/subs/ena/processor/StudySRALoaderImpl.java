package uk.ac.ebi.subs.ena.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Service;
import uk.ac.ebi.ena.sra.StudyInfo;
import uk.ac.ebi.ena.sra.xml.StudyType;
import uk.ac.ebi.subs.data.submittable.ENAStudy;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

/**
 * Created by neilg on 12/04/2017.
 */
public class StudySRALoaderImpl extends AbstractSRALoaderService<ENAStudy> {
    public static final String SCHEMA = "study";

    public StudySRALoaderImpl(String principal, String loginName, Marshaller marshaller) {
        super(principal, loginName, SCHEMA, marshaller);
    }

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
    String getSchema() {
        return "study";
    }
}
