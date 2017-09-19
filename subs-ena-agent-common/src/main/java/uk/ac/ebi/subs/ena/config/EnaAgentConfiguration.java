package uk.ac.ebi.subs.ena.config;

//import org.eclipse.persistence.jaxb.JAXBContextProperties;
//import org.eclipse.persistence.jaxb.MarshallerProperties;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import uk.ac.ebi.ena.authentication.model.AuthResult;
import uk.ac.ebi.ena.sra.SRALoader;
import uk.ac.ebi.subs.data.submittable.ENAExperiment;
import uk.ac.ebi.subs.data.submittable.ENARun;
import uk.ac.ebi.subs.data.submittable.ENASample;
import uk.ac.ebi.subs.data.submittable.ENAStudy;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class EnaAgentConfiguration {

    static String SUBMITTABLE_PACKAGE = "uk.ac.ebi.subs.data.submittable";
    static String COMPONENT_PACKAGE = "uk.ac.ebi.subs.data.component";

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

    @Value("classpath:uk/ac/ebi/subs/data/submittable/sample_mapping.xml")
    Resource sampleMappingResource;

    @Bean(name = "study")
    Jaxb2Marshaller jaxb2StudyMarshaller () throws IOException {
        Class enaStudyClass = ENAStudy.class;
        return getJaxb2Marshaller(studyMappingResource, enaStudyClass);
    }

    @Bean(name = "sample")
    Jaxb2Marshaller jaxb2SampleMarshaller () throws IOException {
        Class enaSampleClass = ENASample.class;
        return getJaxb2Marshaller(sampleMappingResource, enaSampleClass);
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

    @Bean
    public JpaTransactionManager transactionManager()
    {
        return new JpaTransactionManager();
    }


    @Bean
    SRALoader sraLoader(@Value("${ena.transaction_mode}") String transactionMode) {
        SRALoader sraLoader = new SRALoader();
        sraLoader.setTransactionMode(SRALoader.TransactionMode.valueOf(transactionMode));
        return sraLoader;
    }

    @Bean
    AuthResult authResult(@Value("${ena.principal}") String principal, @Value("${ena.login_name}")  String loginName) {
        AuthResult authResult = new AuthResult();
        authResult.setLoginName(loginName);
        authResult.setPrinciple(principal);
        Map<String, Boolean> roles = new HashMap<>();
        roles.put("EGA", false);
        roles.put("SUPER_USER", false);
        authResult.setRoles(roles);
        return authResult;
    }

}
