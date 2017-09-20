package uk.ac.ebi.subs.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.ena.data.Submission;
import uk.ac.ebi.subs.ena.data.SubmittableSRAInfo;
import uk.ac.ebi.subs.ena.repository.SubmissionRepository;

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
public class ENAExportService {

    private static final int PAGE_SIZE = 10000;
    List<ExportService> exportServiceList = new ArrayList<>();
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    ExportServiceProperties exportServiceProperties;
    SubmissionRepository submissionRepository;

    public ENAExportService(SampleExportService sampleExportService,
                            StudyExportService studyExportService,
                            ExperimentExportService experimentExportService,
                            SubmissionRepository submissionRepository,
                            ExportServiceProperties exportServiceProperties) {
        exportServiceList.add(sampleExportService);
        exportServiceList.add(studyExportService);
        exportServiceList.add(experimentExportService);
        this.exportServiceProperties = exportServiceProperties;
        this.submissionRepository = submissionRepository;
    }

    public void export () {
        List<String> exportServiceNameList =  Arrays.asList(exportServiceProperties.getServices());
        String submissionAccountId = exportServiceProperties.getSubmissionAccountId();
        final Path exportDirPath = Paths.get(exportServiceProperties.getPath(), submissionAccountId);

        if (exportServiceProperties.isBySubmission()) {
            Pageable page = new PageRequest(0, PAGE_SIZE);
            long rowCount = submissionRepository.countBySubmissionAccountId(submissionAccountId);

            for (int i = 0; i * PAGE_SIZE < rowCount; i++) {
                final List<Submission> submissionList = submissionRepository.findBySubmissionAccountId(submissionAccountId, page);

                for (Submission submission : submissionList) {
                    for (ExportService exportService : exportServiceList) {
                        if (exportServiceNameList.contains(exportService.getName())) {
                            final Path pathForDate = AbstractExportService.getPathForDate(exportDirPath.resolve("SUBMISSION"), submission);
                            exportService.exportBySubmissionId(pathForDate, submission.getId());
                        }

                    }
                }
                logger.info("dumped submissions " + page.getPageSize() + " records in page " + page.getPageNumber());
                page = page.next();
            }

            submissionRepository.countBySubmissionAccountId(submissionAccountId);

        }

        for (ExportService exportService : exportServiceList) {
            if (exportServiceNameList.contains(exportService.getName())) {
                exportService.export(exportDirPath.resolve(exportService.getName()), submissionAccountId);
            }

        }
    }

}
