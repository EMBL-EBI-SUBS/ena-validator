package uk.ac.ebi.subs.ena.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Component;
import uk.ac.ebi.ena.sra.xml.ID;
import uk.ac.ebi.ena.sra.xml.RECEIPTDocument;
import uk.ac.ebi.subs.data.submittable.ENASample;

/**
 * Created by neilg on 12/04/2017.
 */
@Component
public class SampleSRALoader extends AbstractSRALoaderService<ENASample> {
    public static final String SAMPLE_SCHEMA = "sample";

    public String getSchema() {
        return SAMPLE_SCHEMA;
    }

    @Override
    @Autowired
    @Qualifier("sample")
    public void setMarshaller(Marshaller marshaller) {
        super.setMarshaller(marshaller);
    }

    @Override
    ID[] getIDs(RECEIPTDocument.RECEIPT receipt) {
        return receipt.getSAMPLEArray();
    }
}
