package uk.ac.ebi.subs.ena.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.ena.loader.SRALoaderService;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ENAExperimentProcessor extends AbstractENAProcessor<ENAExperiment> {

    @Autowired
    public void setLoader(SRALoaderService<ENAExperiment> sraLoaderService) {
        this.sraLoaderService = sraLoaderService;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<ProcessingCertificate> processSubmission(SubmissionEnvelope envelope) {
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();
        final List<Assay> assays = envelope.getAssays();
        for (Assay assay : assays) {
            ProcessingCertificate processingCertificate = new ProcessingCertificate(assay, Archive.Ena, ProcessingStatusEnum.Error);;
            try {
                final ENAExperiment enaSubmittable = (ENAExperiment) BaseSubmittableFactory.create(ENAExperiment.class, assay);
                processingCertificate = process(enaSubmittable);
            } catch (IllegalAccessException e) {

            } catch (InstantiationException e) {

            }
            processingCertificateList.add(processingCertificate);
        }
        return processingCertificateList;
    }

    @Override
    public String getSubmittableObjectTypeAsAString() {
        return Assay.class.getSimpleName();
    }

    @Override
    public ENASubmittable convertFromSubmittableToENASubmittable(Submittable submittable) throws InstantiationException, IllegalAccessException {
        return BaseSubmittableFactory.create(ENAExperiment.class, submittable);
    }

}
