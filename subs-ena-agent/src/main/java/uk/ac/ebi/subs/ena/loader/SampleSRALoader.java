package uk.ac.ebi.subs.ena.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.ac.ebi.ena.sra.SampleInfo;
import uk.ac.ebi.ena.sra.StudyInfo;
import uk.ac.ebi.ena.sra.xml.SampleType;
import uk.ac.ebi.ena.sra.xml.StudyType;
import uk.ac.ebi.subs.data.submittable.ENASample;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.ena.processor.SRALoaderAccessionException;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

/**
 * Created by neilg on 12/04/2017.
 */
@Component
public class SampleSRALoader extends AbstractSRALoaderService<ENASample> {
    public static final String SCHEMA = "experiment";

    @Override
    public String executeSRALoader(String submissionXML, String submittableXML, Connection connection) throws Exception {
        String accession = null;
        try {
            if (sraLoader.eraputRestWebin(submissionXML,
                    null, submittableXML, null, null, null, null, null, null, null,
                    null, authResult, null, connection) == 0) {
                final Map<SampleType, SampleInfo> samples = sraLoader.getSamples();
                if (samples != null) {
                    if (samples.values().iterator().hasNext()) {
                        accession = samples.values().iterator().next().getSampleAccession();
                        logger.info("Created ENA sample with accession " + accession);
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
        // get rid of this
        connection.commit();
        return accession;
    }

    String getSchema() {
        return "experiment";
    }

    @Override
    @Autowired
    @Qualifier("sample")
    public void setMarshaller(Marshaller marshaller) {
        super.setMarshaller(marshaller);
    }
}
