package uk.ac.ebi.subs.ena.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;

import uk.ac.ebi.subs.data.FullSubmission;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.data.status.ProcessingStatus;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.List;
import java.util.UUID;

public class AssayProcessor extends AgentProcessor<Assay> {

    static final String EXPERIMENT_SCHEMA = "experiment";


    public AssayProcessor(SubmissionEnvelope submissionEnvelope, Archive archive) {
        super(submissionEnvelope, archive);
    }


    @Override
    ProcessingCertificate processData(Assay submittable, SubmissionEnvelope submissionEnvelope) {
        FullSubmission submission = submissionEnvelope.getSubmission();

        for (SampleUse su : submittable.getSampleUses()){
            SampleRef sr = su.getSampleRef();
            Sample sample = sr.fillIn(submission.getSamples(),submissionEnvelope.getSupportingSamples());

            if (sample != null) {
//                enaSampleRepository.save(sample);
            }
        }

        submittable.getStudyRef().fillIn(submission.getStudies());
        return super.processData(submittable, submissionEnvelope);
    }

    @Override
    protected ProcessingStatus loadData(Assay submittable, SubmissionEnvelope submissionEnvelope) {
        if (submittable.getAccession() == null || submittable.getAccession().isEmpty()) {
            submittable.setAccession("ENA-EXP-" + UUID.randomUUID().toString());
        }
        return new ProcessingStatus(ProcessingStatusEnum.Received);
    }


    @Override
    List<Assay> getSubmittables(FullSubmission fullSubmission) {
        return fullSubmission.getAssays();
    }

    @Override
    protected ProcessingStatus updateData(Assay submittable, SubmissionEnvelope submissionEnvelope) {
        return null;
    }
}
