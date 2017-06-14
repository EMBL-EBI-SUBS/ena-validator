package uk.ac.ebi.subs.ena.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Service;
import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.ValidationMessage;
import uk.ac.ebi.ena.sra.ExperimentInfo;
import uk.ac.ebi.ena.sra.SRALoader;
import uk.ac.ebi.ena.sra.xml.ExperimentType;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.ena.loader.SRALoaderService;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class ENARunProcessor extends AbstractENAProcessor<ENARun> {

    @Autowired
    public void setLoader(SRALoaderService<ENARun> sraLoaderService) {
        this.sraLoaderService = sraLoaderService;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<ProcessingCertificate> processSubmission(SubmissionEnvelope envelope) {
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();
        final List<AssayData> assayDataList = envelope.getAssayData();
        for (AssayData assayData : assayDataList) {
            ProcessingCertificate processingCertificate = new ProcessingCertificate(assayData, Archive.Ena, ProcessingStatusEnum.Error);;
            try {
                final ENARun enaSubmittable = (ENARun) BaseSubmittableFactory.create(ENARun.class, assayData);
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
        return Study.class.getSimpleName();
    }

    @Override
    public Collection<ValidationMessage<Origin>> convertFromSubmittableToENASubmittable(Submittable submittable) throws InstantiationException, IllegalAccessException {
        return null;
    }

}