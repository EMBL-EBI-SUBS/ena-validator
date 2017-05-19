package uk.ac.ebi.subs.ena.processor;

import uk.ac.ebi.subs.data.submittable.ENASubmittable;
import uk.ac.ebi.subs.ena.loader.SRALoaderService;
import uk.ac.ebi.subs.processing.ProcessingCertificate;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by neilg on 17/05/2017.
 */
public interface ENAAgentProcessor<T extends ENASubmittable> extends AgentProcessor<T> {
    @Override
    ProcessingCertificate process(T submittable);
    SRALoaderService<T> getLoader ();
    void setLoader (SRALoaderService<T> sraLoaderService);
    DataSource getDataSource ();
    void setDataSource (DataSource dataSource);
}