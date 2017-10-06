package uk.ac.ebi.subs.export;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.ena.data.Submission;
import uk.ac.ebi.subs.ena.data.SubmittableSRAInfo;
import uk.ac.ebi.subs.ena.repository.SubmissionRepository;
import uk.ac.ebi.subs.stresstest.ClientCompleteSubmission;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableConfigurationProperties(ExportServiceProperties.class)
public class ENASubmissionExportService {

    private static final int PAGE_SIZE = 10000;
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    ExportServiceProperties exportServiceProperties;
    SubmissionRepository submissionRepository;
    ObjectMapper objectMapper;

    public ENASubmissionExportService(SubmissionRepository submissionRepository,
                                      ExportServiceProperties exportServiceProperties,
                                      ObjectMapper objectMapper) {
        this.exportServiceProperties = exportServiceProperties;
        this.submissionRepository = submissionRepository;
        this.objectMapper = objectMapper;
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void export(List<ExportService> exportServiceList, Path exportDirPath, String submissionAccountId) {
        Team team = new Team();
        team.setName(submissionAccountId);
        exportDirPath = exportDirPath.resolve(submissionAccountId);
        Pageable page = new PageRequest(0, PAGE_SIZE);
        long rowCount = submissionRepository.countBySubmissionAccountId(submissionAccountId);

        for (int i = 0; i * PAGE_SIZE < rowCount; i++) {
            final List<Submission> submissionList = submissionRepository.findBySubmissionAccountId(submissionAccountId, page);

            for (Submission submission : submissionList) {
                final Path pathForDate = AbstractExportService.getPathForDate(exportDirPath.resolve("SUBMISSION"), submission);
                ClientCompleteSubmission clientCompleteSubmission = new ClientCompleteSubmission();
                clientCompleteSubmission.setId(submission.getId());
                clientCompleteSubmission.setTeam(team);
                clientCompleteSubmission.setSubmissionDate(submission.getFirstCreated());

                for (ExportService exportService : exportServiceList) {
                    exportService.export(clientCompleteSubmission, submission.getId());
                }

                try {
                    if (clientCompleteSubmission.allSubmissionItems().size() > 0) {
                        final Path exportPath = pathForDate.resolve(submission.getId() + ".json");
                        Files.createDirectories(pathForDate);
                        objectMapper.writeValue(exportPath.toFile(),clientCompleteSubmission);
                    }
                } catch (IOException e) {
                    logger.error("Error exporting " + submission.getId(),e);
                }
            }
            logger.info("dumped submissions " + page.getPageSize() + " records in page " + page.getPageNumber());
            page = page.next();
        }

    }

}
