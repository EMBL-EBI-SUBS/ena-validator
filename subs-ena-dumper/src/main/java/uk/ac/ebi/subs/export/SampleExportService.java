package uk.ac.ebi.subs.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.ENASample;
import uk.ac.ebi.subs.data.submittable.MappingHelper;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.repository.SampleRepository;
import uk.ac.ebi.subs.stresstest.ClientCompleteSubmission;

import java.util.List;

@Service
public class SampleExportService extends AbstractExportService<Sample,ENASample> {

    public static final String SAMPLE_XPATH_EXPRESSION = "/SAMPLE_SET/SAMPLE[1]";

    public SampleExportService(SampleRepository sampleRepository, ObjectMapper objectMapper) {
        super(sampleRepository, ENASample.class, MappingHelper.SAMPLE_MARSHALLER, SAMPLE_XPATH_EXPRESSION,objectMapper);
    }

    @Override
    protected void updateClientCompleteSubmission(ClientCompleteSubmission clientCompleteSubmission, Sample submittable) {
        uk.ac.ebi.subs.data.client.Sample sample = new uk.ac.ebi.subs.data.client.Sample();
        BeanUtils.copyProperties(submittable,sample);
        clientCompleteSubmission.getSamples().add(sample);
    }
}
