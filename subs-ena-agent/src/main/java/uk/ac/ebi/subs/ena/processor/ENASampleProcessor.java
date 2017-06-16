package uk.ac.ebi.subs.ena.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.ValidationMessage;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.ena.loader.SRALoaderService;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ENASampleProcessor extends AbstractENAProcessor<ENASample> {

    @Autowired
    public void setLoader(SRALoaderService<ENASample> sraLoaderService) {
        this.sraLoaderService = sraLoaderService;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<ProcessingCertificate> processSubmission(SubmissionEnvelope envelope) {
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();
        final List<Sample> samples = envelope.getSamples();
        for (Sample sample : samples) {
            try {
                final ENASample enaSubmittable = (ENASample) BaseSubmittableFactory.create(ENASample.class, sample);
                processingCertificateList.add(process(enaSubmittable));
            } catch (IllegalAccessException e) {

            } catch (InstantiationException e) {

            }
        }
        return processingCertificateList;
    }

    @Override
    public String getSubmittableObjectTypeAsAString() {
        return Sample.class.getSimpleName();
    }

    public ENASubmittable convertFromSubmittableToENASubmittable(Submittable submittable) throws InstantiationException, IllegalAccessException {
        return BaseSubmittableFactory.create(ENASample.class, submittable);
    }
}
