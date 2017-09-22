package uk.ac.ebi.subs.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.data.submittable.MappingHelper;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.ena.data.SubmittableSRAInfo;
import uk.ac.ebi.subs.ena.repository.StudyRepository;
import uk.ac.ebi.subs.stresstest.ClientCompleteSubmission;

import javax.xml.bind.JAXBException;
import javax.xml.xpath.XPathExpressionException;
import java.time.LocalDate;
import java.util.List;

@Service
public class StudyExportService extends AbstractExportService<Study,ENAStudy> {

    public static final String STUDY_XPATH_EXPRESSION = "/STUDY_SET/STUDY[1]";

    public StudyExportService(StudyRepository studyRepository, ObjectMapper objectMapper) {
        super(studyRepository, ENAStudy.class, MappingHelper.STUDY_MARSHALLER, STUDY_XPATH_EXPRESSION,objectMapper);
    }

    @Override
    protected Study getSubmittable(SubmittableSRAInfo submittable) throws XPathExpressionException, JAXBException, IllegalAccessException {
        final Study study = super.getSubmittable(submittable);
        LocalDate releaseDate = LocalDate.parse("2017-01-01");
        study.setReleaseDate(java.sql.Date.valueOf(releaseDate));
        return study;
    }

    @Override
    protected void updateClientCompleteSubmission(ClientCompleteSubmission clientCompleteSubmission, Study submittable) {
        uk.ac.ebi.subs.data.client.Study study = new uk.ac.ebi.subs.data.client.Study();
        BeanUtils.copyProperties(submittable,study);
        clientCompleteSubmission.getStudies().add(study);
    }
}
