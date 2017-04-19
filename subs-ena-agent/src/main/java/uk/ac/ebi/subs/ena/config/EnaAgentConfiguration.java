package uk.ac.ebi.subs.ena.config;

//import org.eclipse.persistence.jaxb.JAXBContextProperties;
//import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import uk.ac.ebi.subs.data.submittable.ENAExperiment;
import uk.ac.ebi.subs.data.submittable.ENARun;
import uk.ac.ebi.subs.data.submittable.ENAStudy;
import uk.ac.ebi.subs.ena.processor.SRALoaderService;
import uk.ac.ebi.subs.ena.processor.StudySRALoaderImpl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;


@Configuration
public class EnaAgentConfiguration {

    String SUBMITTABLE_PACKAGE = "uk.ac.ebi.subs.data.submittable";
    String COMPONENT_PACKAGE = "uk.ac.ebi.subs.data.component";

    @Autowired
    private ApplicationContext appContext;

    @Value("${ena.submission_account_id}")
    String submissionAccountId;

    @Value("classpath:uk/ac/ebi/subs/data/component/attribute_mapping.xml")
    Resource componentMappingResource;

    @Value("classpath:uk/ac/ebi/subs/data/submittable/study_mapping.xml")
    Resource studyMappingResource;

    @Value("classpath:uk/ac/ebi/subs/data/submittable/experiment_mapping.xml")
    Resource experimentMappingResource;

    @Value("classpath:uk/ac/ebi/subs/data/submittable/run_mapping.xml")
    Resource runMappingResource;

    @Bean(name = "study")
    Jaxb2Marshaller jaxb2StudyMarshaller () throws IOException {
        Class enaStudyClass = ENAStudy.class;
        return getJaxb2Marshaller(studyMappingResource, enaStudyClass);
    }

    @Bean(name = "experiment")
    Jaxb2Marshaller jaxb2ExperimentMarshaller () throws IOException {
        Class enaExperimentClass = ENAExperiment.class;
        return getJaxb2Marshaller(experimentMappingResource, enaExperimentClass);
    }

    @Bean(name = "run")
    Jaxb2Marshaller jaxb2RunMarshaller () throws IOException {
        Class enaRunClass = ENARun.class;
        return getJaxb2Marshaller(runMappingResource, enaRunClass);
    }

    private Jaxb2Marshaller getJaxb2Marshaller(Resource resource, Class enaStudyClass) throws IOException {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        final HashMap<String, Object> jaxpHashMap = new HashMap<>();
        jaxpHashMap.put(COMPONENT_PACKAGE, new StreamSource(componentMappingResource.getInputStream()));
        jaxpHashMap.put(SUBMITTABLE_PACKAGE, new StreamSource(resource.getInputStream()));
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, jaxpHashMap);
        jaxb2Marshaller.setJaxbContextProperties(properties);
        jaxb2Marshaller.setClassesToBeBound(enaStudyClass);
        return jaxb2Marshaller;
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.erareader_era")
    DataSource dataSource() {
        DataSource dataSource = DataSourceBuilder.create().build();
        return dataSource;
    }

    @Bean(name = "studySRALoader")
    SRALoaderService studySRALoaderService () throws IOException {
        StudySRALoaderImpl studySRALoader = new StudySRALoaderImpl(submissionAccountId,submissionAccountId,jaxb2StudyMarshaller());
        return studySRALoader;
    }

}
