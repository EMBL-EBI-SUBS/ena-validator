package uk.ac.ebi.subs.ena.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Component;
import uk.ac.ebi.ena.sra.SampleInfo;
import uk.ac.ebi.ena.sra.SubmissionObject;
import uk.ac.ebi.ena.sra.SubmissionObjects;
import uk.ac.ebi.ena.sra.xml.SampleType;
import uk.ac.ebi.subs.data.submittable.ENASample;
import uk.ac.ebi.subs.ena.processor.SRALoaderAccessionException;

import java.sql.Connection;
import java.util.List;
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
            sraLoader.eraput(submissionXML,
                    null, submittableXML, null , null, null, null, null, null, null,
                    null, true, authResult, null, connection);
            if (sraLoader.getValidationResult().isValid()) {
                final SubmissionObjects submissionObjects = sraLoader.getSubmissionObjects();
                final List<SubmissionObject.SampleSubmissionObject> submittableObjectList = submissionObjects.getSamples();

                for (SubmissionObject.SubmissionSubmittableObject submissionObject : submittableObjectList) {
                    accession = submissionObject.getInfo().getId();
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
