package uk.ac.ebi.subs.ena.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Component;
import uk.ac.ebi.ena.sra.ExperimentInfo;
import uk.ac.ebi.ena.sra.SubmissionInfo;
import uk.ac.ebi.ena.sra.SubmissionObject;
import uk.ac.ebi.ena.sra.SubmissionObjects;
import uk.ac.ebi.ena.sra.xml.ExperimentType;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.submittable.ENAExperiment;
import uk.ac.ebi.subs.ena.processor.SRALoaderAccessionException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created by neilg on 12/04/2017.
 */
@Component
public class ExperimentSRALoader extends AbstractSRALoaderService<ENAExperiment> {
    public static final String SCHEMA = "experiment";

    @Override
    public String executeSRALoader(String submissionXML, String submittableXML, Connection connection) throws Exception {
        String accession = null;
        try {
            sraLoader.eraput(submissionXML,
                    null, null, submittableXML, null, null, null, null, null, null,
                    null, true, authResult, null, connection);
            if (sraLoader.getValidationResult().isValid()) {
                final SubmissionObjects submissionObjects = sraLoader.getSubmissionObjects();
                final List<SubmissionObject.ExperimentSubmissionObject> experiments1 = submissionObjects.getExperiments();

                for (SubmissionObject.ExperimentSubmissionObject submissionObject : experiments1) {
                    final ExperimentInfo info = submissionObject.getInfo();
                    accession = info.getId();
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
    @Qualifier("experiment")
    public void setMarshaller(Marshaller marshaller) {
        super.setMarshaller(marshaller);
    }
}
