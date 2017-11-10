package uk.ac.ebi.subs.ena.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Component;
import uk.ac.ebi.ena.sra.xml.ID;
import uk.ac.ebi.ena.sra.xml.RECEIPTDocument;
import uk.ac.ebi.subs.data.submittable.ENARun;

/**
 * Created by neilg on 22/05/2017.
 */
@Component
public class RunSRALoader extends AbstractSRALoaderService<ENARun> {
    public static final String RUN_SCHEMA = "run";

    @Override
    public String getSchema() {
        return RUN_SCHEMA;
    }

    @Override
    @Autowired
    @Qualifier("run")
    public void setMarshaller(Marshaller marshaller) {
        super.setMarshaller(marshaller);
    }

    @Override
    ID[] getIDs(RECEIPTDocument.RECEIPT receipt) {
        return receipt.getRUNArray();
    }
}
