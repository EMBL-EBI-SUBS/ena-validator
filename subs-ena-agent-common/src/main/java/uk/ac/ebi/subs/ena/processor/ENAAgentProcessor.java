package uk.ac.ebi.subs.ena.processor;

import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.ValidationMessage;
import uk.ac.ebi.subs.data.submittable.ENASubmittable;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.ena.loader.SRALoaderService;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

/**
 * Created by neilg on 17/05/2017.
 */
public interface ENAAgentProcessor<T extends ENASubmittable> extends AgentProcessor<T> {
    ProcessingCertificate processAndConvertSubmittable(Submittable submittable, List<SingleValidationResult> singleValidationResultList);
    List<? extends Submittable> getSubmittables (SubmissionEnvelope submissionEnvelope);
    SRALoaderService<T> getLoader ();
    void setLoader (SRALoaderService<T> sraLoaderService);
    DataSource getDataSource ();
    void setDataSource (DataSource dataSource);
    String getSubmittableObjectTypeAsAString();
    Collection<SingleValidationResult> validateEntity(T enaSubmittable);
    T convertFromSubmittableToENASubmittable(Submittable submittable, Collection<SingleValidationResult> singleValidationResultList)
            throws InstantiationException, IllegalAccessException;
    String getName();
}