package uk.ac.ebi.subs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import uk.ac.ebi.subs.export.ENAExportService;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootConfiguration
@EnableAutoConfiguration(exclude={MongoRepositoriesAutoConfiguration.class,MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public class ExportApp implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ExportApp.class);

    ENAExportService enaExportService;

    public ExportApp(ENAExportService enaExportService) {
        this.enaExportService = enaExportService;
    }

    @Override
    public void run(String... args) {
        enaExportService.export();
    }

    public static void main(String[] args) {
        SpringApplication.run(ExportApp.class, args);
    }
}
