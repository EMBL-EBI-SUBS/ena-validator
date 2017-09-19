package uk.ac.ebi.subs.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableConfigurationProperties(ENAExportServiceConfiguration.class)
public class ENAExportService {

    List<ExportService> exportServiceList = new ArrayList<>();
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    ENAExportServiceConfiguration enaExportServiceConfiguration;


    public ENAExportService(SampleExportService sampleExportService,
                            StudyExportService studyExportService,
                            ExperimentExportService experimentExportService,
                            ENAExportServiceConfiguration enaExportServiceConfiguration) {
        exportServiceList.add(sampleExportService);
        exportServiceList.add(studyExportService);
        exportServiceList.add(experimentExportService);
        this.enaExportServiceConfiguration = enaExportServiceConfiguration;
    }

    public void export () {
        List<String> exportServiceNameList =  Arrays.asList(enaExportServiceConfiguration.getServices());
        String submissionAccountId = enaExportServiceConfiguration.getSubmissionAccountId();
        final Path exportDirPath = Paths.get(enaExportServiceConfiguration.getPath(), submissionAccountId);

        for (ExportService exportService : exportServiceList) {
            if (exportServiceNameList.contains(exportService.getName())) {
                exportService.export(exportDirPath.resolve(exportService.getName()), submissionAccountId);
            }

        }
    }

}
