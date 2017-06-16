package uk.ac.ebi.subs.ena.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.ValidationMessage;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.ena.loader.SRALoaderService;
import uk.ac.ebi.subs.ena.loader.StudySRALoader;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ENAStudyProcessor extends AbstractENAProcessor<ENAStudy>  {

    @Autowired
    public void setLoader(SRALoaderService<ENAStudy> sraLoaderService) {
        this.sraLoaderService = sraLoaderService;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<ProcessingCertificate> processSubmission(SubmissionEnvelope envelope) {
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();
        final List<Study> studies = envelope.getStudies();
        for (Study study : studies) {
            ProcessingCertificate processingCertificate = new ProcessingCertificate(study, Archive.Ena, ProcessingStatusEnum.Error);;
            try {
                final ENAStudy enaSubmittable = (ENAStudy) BaseSubmittableFactory.create(ENAStudy.class, study);
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

    public ENASubmittable convertFromSubmittableToENASubmittable(Submittable submittable) throws InstantiationException, IllegalAccessException {
        return BaseSubmittableFactory.create(ENAStudy.class, submittable);
    }

}
