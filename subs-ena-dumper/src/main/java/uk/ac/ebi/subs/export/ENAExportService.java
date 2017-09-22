package uk.ac.ebi.subs.export;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.ena.repository.SubmissionRepository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@EnableConfigurationProperties(ExportServiceProperties.class)
public class ENAExportService {

    List<ExportService> exportServiceList = new ArrayList<>();
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    ExportServiceProperties exportServiceProperties;
    ENASubmissionExportService enaSubmissionExportService;
    ENASubmittableExportService enaSubmittableExportService;

    public ENAExportService(ENASubmissionExportService enaSubmissionExportService,
                            ENASubmittableExportService enaSubmittableExportService,
                            SampleExportService sampleExportService,
                            StudyExportService studyExportService,
                            ExperimentExportService experimentExportService,
                            ExportServiceProperties exportServiceProperties) {
        this.enaSubmissionExportService = enaSubmissionExportService;
        this.enaSubmittableExportService = enaSubmittableExportService;
        this.exportServiceProperties = exportServiceProperties;
        ExportService [] exportServices = {sampleExportService,studyExportService,experimentExportService};
        setExportServiceList(Arrays.asList(exportServices),Arrays.asList(exportServiceProperties.getServices()));
    }

    public void setExportServiceList(List<ExportService> exportServiceList, List<String> servicesNamesList) {
        for (ExportService exportService : exportServiceList) {
            if (servicesNamesList.contains(exportService.getName())) {
                this.exportServiceList.add(exportService);
            }
        }
        this.exportServiceList = exportServiceList;
    }

    public void export () {
        if (exportServiceProperties.isCompleteSubmissionFormat()) {
            enaSubmissionExportService.export(exportServiceList, Paths.get(exportServiceProperties.getPath()),exportServiceProperties.getSubmissionAccountId());
        } else {
            enaSubmittableExportService.export(exportServiceList, Paths.get(exportServiceProperties.getPath()),exportServiceProperties.getSubmissionAccountId());
        }
    }
}
