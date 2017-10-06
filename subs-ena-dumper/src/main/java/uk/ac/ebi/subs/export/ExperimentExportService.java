package uk.ac.ebi.subs.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.ENAExperiment;
import uk.ac.ebi.subs.data.submittable.MappingHelper;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.ena.repository.ExperimentRepository;
import uk.ac.ebi.subs.stresstest.ClientCompleteSubmission;

import java.util.List;

@Service
public class ExperimentExportService extends AbstractExportService<Assay,ENAExperiment> {

    public static final String EXPERIMENT_XPATH_EXPRESSION = "/EXPERIMENT_SET/EXPERIMENT[1]";

    public ExperimentExportService(ExperimentRepository experimentRepository, ObjectMapper objectMapper) {
        super(experimentRepository, ENAExperiment.class, MappingHelper.EXPERIMENT_MARSHALLER, EXPERIMENT_XPATH_EXPRESSION,objectMapper);
    }

    @Override
    protected void updateClientCompleteSubmission(ClientCompleteSubmission clientCompleteSubmission, Assay submittable) {
        uk.ac.ebi.subs.data.client.Assay assay = new uk.ac.ebi.subs.data.client.Assay();
        BeanUtils.copyProperties(submittable,assay);
        clientCompleteSubmission.getAssays().add(assay);
    }
}
