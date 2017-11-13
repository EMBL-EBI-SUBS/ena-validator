package uk.ac.ebi.subs.ena.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.ena.loader.SRALoaderService;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

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

    @Override
    public String getSubmittableObjectTypeAsAString() {
        return Sample.class.getSimpleName();
    }

    @Override
    public ENASample convertFromSubmittableToENASubmittable(Submittable submittable,Collection<SingleValidationResult> singleValidationResultList) throws InstantiationException, IllegalAccessException {
        final ENASample enaSample = ENASubmittable.create(ENASample.class, submittable);
        singleValidationResultList.addAll(enaSample.getValidationResultList());
        return enaSample;
    }

    @Override
    public List<Sample> getSubmittables(SubmissionEnvelope submissionEnvelope) {
        return submissionEnvelope.getSamples();
    }
}
