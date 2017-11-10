package uk.ac.ebi.subs.ena.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Component;
import uk.ac.ebi.ena.sra.xml.ID;
import uk.ac.ebi.ena.sra.xml.RECEIPTDocument;
import uk.ac.ebi.subs.data.submittable.ENAExperiment;

/**
 * Created by neilg on 12/04/2017.
 */
@Component
public class ExperimentSRALoader extends AbstractSRALoaderService<ENAExperiment> {
    public static final String EXPERIMENT_SCHEMA = "experiment";

    public String getSchema() {
        return EXPERIMENT_SCHEMA;
    }

    @Override
    @Autowired
    @Qualifier("experiment")
    public void setMarshaller(Marshaller marshaller) {
        super.setMarshaller(marshaller);
    }

    @Override
    ID[] getIDs(RECEIPTDocument.RECEIPT receipt) {
        return receipt.getEXPERIMENTArray();
    }
}
