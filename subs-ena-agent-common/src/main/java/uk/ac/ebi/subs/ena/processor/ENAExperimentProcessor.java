package uk.ac.ebi.subs.ena.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.ena.loader.SRALoaderService;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ENAExperimentProcessor extends AbstractENAProcessor<ENAExperiment> {

    @Autowired
    public void setLoader(SRALoaderService<ENAExperiment> sraLoaderService) {
        this.sraLoaderService = sraLoaderService;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getSubmittableObjectTypeAsAString() {
        return Assay.class.getSimpleName();
    }

    private void updateNullTeamName (Assay assay) {
        if (assay.getStudyRef().getTeam() == null) {
            assay.getStudyRef().setTeam(assay.getTeam().getName());
        }
        for (SampleUse sampleUse : assay.getSampleUses()) {
            if (sampleUse.getSampleRef().getTeam() == null) {
                sampleUse.getSampleRef().setTeam(assay.getTeam().getName());
            }
        }
    }

    @Override
    public ENAExperiment convertFromSubmittableToENASubmittable(Submittable submittable,Collection<SingleValidationResult> singleValidationResultList) throws InstantiationException, IllegalAccessException {

        final ENAExperiment enaExperiment = (ENAExperiment) BaseSubmittableFactory.create(ENAExperiment.class, submittable);
        singleValidationResultList.addAll(enaExperiment.getValidationResultList());
        return enaExperiment;
    }

    @Override
    public List<Assay> getSubmittables(SubmissionEnvelope submissionEnvelope) {
        return submissionEnvelope.getAssays();
    }

}
