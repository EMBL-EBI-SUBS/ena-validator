package uk.ac.ebi.subs.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.ena.data.Submission;
import uk.ac.ebi.subs.ena.data.SubmittableSRAInfo;
import uk.ac.ebi.subs.ena.repository.SubmissionRepository;
import uk.ac.ebi.subs.stresstest.ClientCompleteSubmission;

import javax.xml.bind.JAXBException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableConfigurationProperties(ExportServiceProperties.class)
public class ENASubmittableExportService {

    private static final int PAGE_SIZE = 10000;
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    ExportServiceProperties exportServiceProperties;

    public ENASubmittableExportService(ExportServiceProperties exportServiceProperties) {
        this.exportServiceProperties = exportServiceProperties;
    }

    public void export (List<ExportService> exportServiceList, Path exportDirPath, String submissionAccountId  ) {

        for (ExportService exportService : exportServiceList) {
                exportService.export(exportDirPath.resolve(exportService.getName()), submissionAccountId);
        }
    }

}
