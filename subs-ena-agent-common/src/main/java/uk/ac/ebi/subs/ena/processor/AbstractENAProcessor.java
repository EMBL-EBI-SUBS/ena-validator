package uk.ac.ebi.subs.ena.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.ena.sra.xml.RECEIPTDocument;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.ENASubmittable;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.ena.loader.SRALoaderService;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by neilg on 19/05/2017.
 */
public abstract class AbstractENAProcessor<T extends ENASubmittable> implements ENAAgentProcessor<T> {

    protected static final Logger logger = LoggerFactory.getLogger(ENAStudyProcessor.class);
    protected SRALoaderService<T> sraLoaderService;
    protected DataSource dataSource;

    @Override
    public SRALoaderService<T> getLoader() {
        return sraLoaderService;
    }

    public ProcessingCertificate processAndConvertSubmittable(Submittable submittable, List<SingleValidationResult> singleValidationResultList) {
        ProcessingCertificate processingCertificate = new ProcessingCertificate(submittable, Archive.Ena, ProcessingStatusEnum.Error);
        try {
            final T enaSubmittable = convertFromSubmittableToENASubmittable(submittable, singleValidationResultList);
            processingCertificate = process(enaSubmittable);
        } catch (InstantiationException e) {
            logger.error("convertFromSubmittableToENASubmittable error ",e);
        } catch (IllegalAccessException e) {
            logger.error("convertFromSubmittableToENASubmittable error ",e);
        }
        return processingCertificate;
    }

    @Override
    public ProcessingCertificate process(T submittable) {
        //Connection connection = DataSourceUtils.getConnection(dataSource);

        ProcessingCertificate processingCertificate = new ProcessingCertificate(submittable, Archive.Ena, ProcessingStatusEnum.Error);

        try {
            sraLoaderService.executeSRASubmission(submittable,submittable.getAlias(),false);
            processingCertificate = new ProcessingCertificate(submittable, Archive.Ena, ProcessingStatusEnum.Received,submittable.getAccession());
        } catch (Exception e) {
            logger.error("Error processing submittable : " + submittable.getId(),e);
        }
        return processingCertificate;
    }

    /**
     * Gets the validation messages via the SRA Validator.
     *
     * @param enaSubmittable entity to validate
     * @return a {@link Collection} of
     */
    public Collection<SingleValidationResult> validateEntity(T enaSubmittable) {
        logger.info("Validation started for {} entity with id: {}", enaSubmittable.getClass().getSimpleName(),
                enaSubmittable.getId());

        Collection<SingleValidationResult> singleValidationResultCollection = new ArrayList<>();
        //ValidationResult validationResult = getValidationResult(enaSubmittable);
        singleValidationResultCollection.addAll(convertValidationMessages(enaSubmittable.getId().toString(), null));
            //final Collection<ValidationMessage<Origin>> messages = validationResult.getMessages();
        return singleValidationResultCollection;
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

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Converts a collection of SRA validation messages to a collection {@link SingleValidationResult}
     * @param {@link Collection} of
     * @return {@link Collection} of {@link SingleValidationResult}
     */
    Collection<SingleValidationResult> convertValidationMessages (String entityUUID, RECEIPTDocument.RECEIPT.MESSAGES messages) {
        Collection<SingleValidationResult> singleValidationResultCollection = new ArrayList<>();
        /*
        for (ValidationMessage<Origin> validationMessage : validationResult.getMessages()) {
            SingleValidationResult singleValidationResult = new SingleValidationResult(ValidationAuthor.Ena,entityUUID);
            singleValidationResult.setMessage(validationMessage.getMessage());
            if (validationMessage.getSeverity().equals(Severity.ERROR)) {
                singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
            } else if (validationMessage.getSeverity().equals(Severity.WARNING)) {
                singleValidationResult.setValidationStatus(SingleValidationResultStatus.Warning);
            } else if (validationMessage.getSeverity().equals(Severity.FIX)) {
                singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);

            }
        }
        */
        return singleValidationResultCollection;
    }

    abstract String getSubmittableObjectTypeAsAString();

}


