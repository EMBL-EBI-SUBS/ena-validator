package uk.ac.ebi.subs.ena.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.oxm.Marshaller;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.ebi.subs.data.component.AssayRef;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.ena.EnaAgentApplication;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by neilg on 07/04/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class EnaAgentConfigurationTest {

    @Autowired
    @Qualifier("study")
    Marshaller studyMarshaller;

    @Autowired
    @Qualifier("experiment")
    Marshaller experimentMarshaller;

    @Autowired
    @Qualifier("run")
    Marshaller runMarshaller;

    @Autowired
    DataSource dataSource;

    static final String team = "my-team";

    DocumentBuilderFactory documentBuilderFactory = null;
    XPathFactory xPathFactory = null;
    ObjectMapper objectMapper = new ObjectMapper();
    Marshaller marshaller;

    @Before
    public void setUp() throws IOException, JAXBException, URISyntaxException {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        xPathFactory = XPathFactory.newInstance();
    }

    @Test
    public void testJaxb2StudyMarshaller() throws Exception {
        Study study = createStudy(UUID.randomUUID().toString(),team);
        ENAStudy enaStudy = new ENAStudy(study);
        final Document document = documentBuilderFactory.newDocumentBuilder().newDocument();
        studyMarshaller.marshal(enaStudy,new DOMResult(document));
        final Element documentElement = document.getDocumentElement();
        assertNotNull(documentElement.getLocalName());
    }

    @Test
    public void testJaxb2ExperimentMarshaller() throws Exception {
        Assay assay = createAssay(UUID.randomUUID().toString(),team);
        ENAExperiment enaExperiment = new ENAExperiment(assay);
        final Document document = documentBuilderFactory.newDocumentBuilder().newDocument();
        experimentMarshaller.marshal(enaExperiment,new DOMResult(document));
        final Element documentElement = document.getDocumentElement();
        assertNotNull(documentElement.getLocalName());
    }

    @Test
    public void testJaxb2RunMarshaller() throws Exception {
        AssayData assayData = createAssayData(UUID.randomUUID().toString(),team);
        ENARun enaRun = new ENARun(assayData);
        final Document document = documentBuilderFactory.newDocumentBuilder().newDocument();
        runMarshaller.marshal(enaRun,new DOMResult(document));
        final Element documentElement = document.getDocumentElement();
        assertNotNull(documentElement.getLocalName());
    }


    @Test
    public void testDataSource() throws Exception {
        Connection connection = dataSource.getConnection();
        final boolean valid = connection.isValid(100);
        connection.close();
        assertTrue(valid);
    }



    public Study createStudy (String name, String team) {
        Study study = new Study();
        study.setAlias(name);
        study.setTeam(createTeam(team));
        study.setDescription("Description");
        study.setTitle("Title");
        Attribute attribute = new Attribute();
        attribute.setName("existing_study_type");
        attribute.setValue(UUID.randomUUID().toString());
        study.getAttributes().add(attribute);
        return study;
    }

    public Assay createAssay (String name, String team) {
        Assay assay = new Assay();
        assay.setAlias(name);
        assay.setTeam(createTeam(team));
        assay.setDescription("Description");
        assay.setTitle("Title");
        Attribute libraryNameAttribute = new Attribute();
        libraryNameAttribute.setName(ENAExperiment.LIBRARY_NAME);
        libraryNameAttribute.setValue(ENAExperiment.LIBRARY_NAME);
        assay.getAttributes().add(libraryNameAttribute);
        Attribute designDescriptionAttribute = new Attribute();
        designDescriptionAttribute.setName(ENAExperiment.DESIGN_DESCRIPTION);
        designDescriptionAttribute.setValue(ENAExperiment.DESIGN_DESCRIPTION);
        assay.getAttributes().add(designDescriptionAttribute);
        Attribute libraryLayoutAttribute = new Attribute();
        libraryLayoutAttribute.setName(ENAExperiment.LIBRARY_LAYOUT);
        libraryLayoutAttribute.setValue(ENAExperiment.SINGLE);
        assay.getAttributes().add(libraryLayoutAttribute);
        Attribute libraryStratagy = new Attribute();
        libraryStratagy.setName(ENAExperiment.LIBRARY_STRATEGY);
        libraryStratagy.setValue("WGS");
        assay.getAttributes().add(libraryStratagy);
        Attribute librarySource = new Attribute();
        librarySource.setName(ENAExperiment.LIBRARY_SOURCE);
        librarySource.setValue("Genomic");
        Attribute librarySelection = new Attribute();
        librarySelection.setName(ENAExperiment.LIBRARY_SELECTION);
        librarySelection.setValue("Random");
        assay.getAttributes().add(librarySelection);
        Attribute platformAtribute = new Attribute();
        platformAtribute.setName(ENAExperiment.PLATFORM_TYPE);
        platformAtribute.setValue("ILLUMINA");
        assay.getAttributes().add(platformAtribute);
        Attribute instrumentModelAttribute = new Attribute();
        instrumentModelAttribute.setName(ENAExperiment.INSTRUMENT_MODEL);
        instrumentModelAttribute.setValue("Illumina Genome Analyzer");
        assay.getAttributes().add(instrumentModelAttribute);
        return assay;
    }

    public AssayData createAssayData (String name, String team) {
        AssayData assayData = new AssayData();
        assayData.setAlias(name);
        assayData.setTeam(createTeam(team));
        assayData.setDescription("Description");
        assayData.setTitle("Title");
        uk.ac.ebi.subs.data.component.File file = new uk.ac.ebi.subs.data.component.File();
        file.setName("test.fastq");
        file.setChecksumMethod("MD5");
        file.setChecksum("12345678123456781234567812345678");
        file.setType("fastq");
        assayData.getFiles().add(file);
        AssayRef assayRef = new AssayRef();
        assayRef.setAccession(UUID.randomUUID().toString());
        assayData.setAssayRef(assayRef);
        return assayData;
    }

    public Team createTeam (String teamString) {
        Team team = new Team();
        team.setName(teamString);
        return team;
    }


}
