package uk.ac.ebi.subs.ena;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Service;

import uk.ac.ebi.ena.sra.SRALoader;
import uk.ac.ebi.subs.data.FullSubmission;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.ena.processor.ENAExperimentProcessor;
import uk.ac.ebi.subs.ena.processor.ENAStudyProcessor;
import uk.ac.ebi.subs.processing.*;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.messaging.Queues;
import uk.ac.ebi.subs.messaging.Topics;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import javax.sql.DataSource;


@Service
public class EnaAgentSubmissionsProcessor {

    private static final Logger logger = LoggerFactory.getLogger(EnaAgentSubmissionsProcessor.class);

    ProcessingStatusEnum processingStatus = ProcessingStatusEnum.Received;


    RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    @Qualifier("study")
    Marshaller studyMarshaller;

    @Autowired
    @Qualifier("experiment")
    Marshaller experimentMarshaller;

    @Autowired
    @Qualifier("run")
    Marshaller runMarshaller;

    @Autowired
    DataSource dataSource;

    @Value("${ena.submission_account_id}")
    String submissionAccountId;

    SRALoader.TransactionMode transactionMode;


    @Autowired
    public EnaAgentSubmissionsProcessor(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
    }

    @RabbitListener(queues = Queues.ENA_SAMPLES_UPDATED)
    public void handleSampleUpdate(UpdatedSamplesEnvelope updatedSamplesEnvelope){
        logger.info("received updated samples for submission {}",updatedSamplesEnvelope.getSubmissionId());

        updatedSamplesEnvelope.getUpdatedSamples().forEach( s ->{

            logger.info("NOT IMPLEMENTED, updates sample {} using submission {}",s.getAccession(),updatedSamplesEnvelope.getSubmissionId());
        });

        logger.info("finished updating samples for submission {}", updatedSamplesEnvelope.getSubmissionId());
    }


    @RabbitListener(queues = {Queues.ENA_AGENT})
    public void handleSubmission(SubmissionEnvelope submissionEnvelope) {
        logger.info("received submission {}, most recent handler was ",
                submissionEnvelope.getSubmission().getId());
        final ProcessingCertificateEnvelope processingCertificateEnvelope = processSubmission(submissionEnvelope);
        logger.info("processed submission {}",submissionEnvelope.getSubmission().getId());
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS,Topics.EVENT_SUBMISSION_AGENT_RESULTS, processingCertificateEnvelope);
        logger.info("sent submission {}",submissionEnvelope.getSubmission().getId());

    }

    public List<ProcessingCertificate> processSubmission(SubmissionEnvelope submissionEnvelope, Connection connection) {
        List<ProcessingCertificate> certs = new ArrayList<>();
        ENAStudyProcessor ENAStudyProcessor = new ENAStudyProcessor(submissionEnvelope,studyMarshaller,connection,submissionAccountId,transactionMode);
        //certs.addAll(ENAStudyProcessor.processSubmittables());
        ENAExperimentProcessor ENAExperimentProcessor = new ENAExperimentProcessor(submissionEnvelope,experimentMarshaller,connection,submissionAccountId,transactionMode);
        //certs.addAll(ENAExperimentProcessor.processSubmittables());

        return certs;
    }

    public ProcessingCertificateEnvelope processSubmission(SubmissionEnvelope submissionEnvelope) {
        List<ProcessingCertificate> certs = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            certs = processSubmission(submissionEnvelope,connection);
        } catch (SQLException e) {
            logger.error("Failed to get connection from datasource " + dataSource);
            throw rethrow(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error closing connection");
            }
        }

        return new ProcessingCertificateEnvelope(submissionEnvelope.getSubmission().getId(),certs);
    }


    private ProcessingCertificate processStudy(Study study, SubmissionEnvelope submissionEnvelope) {

        String submissionId = submissionEnvelope.getSubmission().getId();

        if (study.getAccession() == null || study.getAccession().isEmpty()) {
            // insert study
        } else {
            // update study
        }

        if (!study.isAccessioned()) {
            study.setAccession("ENA-STU-" + UUID.randomUUID());
        }


        return new ProcessingCertificate(study,Archive.Ena, ProcessingStatusEnum.Received, study.getAccession());
    }


    private ProcessingCertificate processAssay(Assay assay, SubmissionEnvelope submissionEnvelope) {
        FullSubmission submission = submissionEnvelope.getSubmission();

        for (SampleUse su : assay.getSampleUses()){
            SampleRef sr = su.getSampleRef();
            Sample sample = sr.fillIn(submission.getSamples(),submissionEnvelope.getSupportingSamples());

            if (sample != null) {

            }
        }

        assay.getStudyRef().fillIn(submission.getStudies());
        if (!assay.isAccessioned()) {
            assay.setAccession("ENA-EXP-" + UUID.randomUUID());
        }


        return new ProcessingCertificate(assay,Archive.Ena, ProcessingStatusEnum.Received, assay.getAccession());
    }

    private ProcessingCertificate processAssayData(AssayData assayData, SubmissionEnvelope submissionEnvelope) {
        assayData.getAssayRef().fillIn(submissionEnvelope.getSubmission().getAssays());

        if (!assayData.isAccessioned()) {
            assayData.setAccession("ENA-RUN-" + UUID.randomUUID());
        }

        return new ProcessingCertificate(assayData,Archive.Ena, ProcessingStatusEnum.Received, assayData.getAccession());
    }

    /**
     * Cast a CheckedException as an unchecked one.
     *
     * @param throwable to cast
     * @param <T>       the type of the Throwable
     * @return this method will never return a Throwable instance, it will just throw it.
     * @throws T the throwable as an unchecked throwable
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException rethrow(Throwable throwable) throws T {
        throw (T) throwable; // rely on vacuous cast
    }

    @Value("${ena.transaction_mode}")
    public void setTransactionMode (String transactionMode) {
        this.transactionMode = SRALoader.TransactionMode.valueOf(transactionMode.toUpperCase());
    }
}