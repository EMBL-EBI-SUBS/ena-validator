package uk.ac.ebi.subs.ena.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.data.submittable.ENASubmittable;
import uk.ac.ebi.subs.ena.loader.SRALoaderService;
import uk.ac.ebi.subs.processing.ProcessingCertificate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

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

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public ProcessingCertificate process(T submittable) {
        Connection connection = getConnection(dataSource);
        ProcessingCertificate processingCertificate = new ProcessingCertificate(submittable, Archive.Ena, ProcessingStatusEnum.Error);
        try {
            sraLoaderService.executeSubmittableSRALoader(submittable,submittable.getAlias(),connection);
            processingCertificate = new ProcessingCertificate(submittable, Archive.Ena, ProcessingStatusEnum.Received,submittable.getAccession());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processingCertificate;
    }

    protected Connection getConnection(DataSource dataSource) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Failed to get connection from datasource " + dataSource);
            throw rethrow(e);
        }
        return connection;
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
}