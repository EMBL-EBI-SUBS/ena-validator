package uk.ac.ebi.subs.ena.processor;

import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.repository.MongoRepository;

import uk.ac.ebi.subs.data.FullSubmission;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatus;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AgentProcessor<T extends Submittable> {
    private Archive archive;
    SubmissionEnvelope submissionEnvelope;
    static final Logger logger = LoggerFactory.getLogger(AgentProcessor.class);

    public AgentProcessor(SubmissionEnvelope submissionEnvelope, Archive archive) {
        this.submissionEnvelope = submissionEnvelope;
        this.archive = archive;
    }

    ProcessingCertificate processData (T submittable, SubmissionEnvelope submissionEnvelope) {
        try {
            if (isUpdate(submittable)) {
                updateData(submittable, submissionEnvelope);
            } else {
                loadData(submittable, submissionEnvelope);
            }

        } catch (Exception e) {
            logger.error("Error loading submittable " + submittable.getClass().getName() + " id " + submittable.getId(),e);
            return new ProcessingCertificate(submittable,archive, ProcessingStatusEnum.Error,null);
        }
        return new ProcessingCertificate(submittable,archive, ProcessingStatusEnum.Received,submittable.getAccession());
    }

    boolean isUpdate(T submittable) {
        if (submittable.getAccession() == null || submittable.getAccession().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    protected abstract ProcessingStatus loadData(T submittable, SubmissionEnvelope submissionEnvelope) throws Exception;

    protected abstract ProcessingStatus updateData(T submittable, SubmissionEnvelope submissionEnvelope) throws Exception;

    abstract List<T> getSubmittables (FullSubmission fullSubmission);

    public List<ProcessingCertificate> processSubmittables () {
        List<ProcessingCertificate> certs = new ArrayList<>();
        getSubmittables(submissionEnvelope.getSubmission()).stream().
                filter(s -> s.getArchive() == archive)
                .forEach(s -> certs.add(processData(s, submissionEnvelope)));
        return certs;
    }


    public static <T extends Throwable> RuntimeException rethrow(Throwable throwable) throws T {
        throw (T) throwable; // rely on vacuous cast
    }

}
