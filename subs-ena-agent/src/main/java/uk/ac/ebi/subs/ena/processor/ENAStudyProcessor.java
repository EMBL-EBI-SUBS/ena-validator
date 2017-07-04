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
            try {
                final ENAStudy enaSubmittable = (ENAStudy) convertFromSubmittableToENASubmittable(study);
                processingCertificateList.add(process(enaSubmittable));
            } catch (IllegalAccessException e) {

            } catch (InstantiationException e) {

            }
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
