package uk.ac.ebi.subs.ena.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Component;
import uk.ac.ebi.ena.sra.RunInfo;
import uk.ac.ebi.ena.sra.xml.RunType;
import uk.ac.ebi.subs.data.submittable.ENARun;
import uk.ac.ebi.subs.ena.processor.SRALoaderAccessionException;

import java.sql.Connection;
import java.util.Map;

/**
 * Created by neilg on 22/05/2017.
 */
@Component
public class RunSRALoader extends AbstractSRALoaderService<ENARun> {

    @Override
    public String executeSRALoader(String submissionXML, String submittableXML, Connection connection) throws Exception {
        String accession = null;
        try {
            if (sraLoader.eraputRestWebin(submissionXML,
                    null, null, null, submittableXML, null, null, null, null, null,
                    null, authResult, null, connection) == 0) {
                final Map<RunType, RunInfo> runs = sraLoader.getRuns();
                if (runs != null) {
                    if (runs.values().iterator().hasNext()) {
                        accession = runs.values().iterator().next().getRunAccession();
                        logger.info("Created ENA run with accession " + accession);
                    }
                }
            } else {
                logValidationErrors();
            }
        } catch (Exception e) {
            throw new SRALoaderAccessionException(submissionXML, submittableXML);
        }
        if (accession == null) {
            throw new SRALoaderAccessionException(submissionXML, submittableXML);
        }
        return accession;
    }

    @Override
    String getSchema() {
        return "run";
    }

    @Override
    @Autowired
    @Qualifier("run")
    public void setMarshaller(Marshaller marshaller) {
        super.setMarshaller(marshaller);
    }
}
