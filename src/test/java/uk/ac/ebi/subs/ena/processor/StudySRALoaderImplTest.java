package uk.ac.ebi.subs.ena.processor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import uk.ac.ebi.subs.EnaAgentApplication;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

import static org.junit.Assert.*;

/**
 * Created by neilg on 12/04/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {EnaAgentApplication.class})
public class StudySRALoaderImplTest {
    static final String ALIAS_ATTRIBUTE_NAME = "alias";
    static final String CENTER_NAME_ATTRIBUTE_NAME = "center_name";
    static final String BROKER_NAME_ATTRIBUTE_NAME = "broker_name";
    static final String STUDY_TEMPLATE_XML = "/uk/ac/ebi/subs/ena/xml/study_template.xml";
    static final String STUDY_SUBMISSION_TEMPLATE_XML = "/uk/ac/ebi/subs/ena/xml/study_submission_template.xml";

    private static final Logger logger = LoggerFactory.getLogger(StudySRALoaderImplTest.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    @Qualifier("studySRALoader")
    SRALoaderService sraLoaderService;

    Connection connection;

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    XPathFactory xPathFactory = XPathFactory.newInstance();
    XPath xpath = xPathFactory.newXPath();

    @Before
    public void setUp() throws Exception {
        connection = dataSource.getConnection();
        builder = factory.newDocumentBuilder();
    }

    @After
    public void tearDown() throws Exception {
        connection.rollback();
        connection.close();
    }

    @Test
    public void executeSRALoader() throws Exception {
    }

    protected Document getDocument(String resourceName) throws IOException, SAXException {
        InputStream inputStream = getClass().getResourceAsStream(resourceName);
        if (inputStream == null) {
            logger.error("Could not open resource file " + resourceName);
            throw new IOException("Could not open resource file " + resourceName);
        }
        return builder.parse(inputStream);
    }

    protected void updateAttribute(Document document, String attributeName, String attributeValue, boolean append) throws Exception {
        String xpathQuery = "//*[@" + attributeName + "]";
        updateAttribute(document, xpathQuery, attributeName, attributeValue, append);
    }

    protected void updateAttribute(Document document, String xpathQuery, String attributeName, String attributeValue, boolean append) throws Exception {
        final XPathExpression xpathExpression = xpath.compile(xpathQuery);
        final NodeList nodeList = (NodeList) xpathExpression.evaluate(document, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nodeList.item(i);
                final NamedNodeMap attributes = element.getAttributes();
                Node nodeAttr = attributes.getNamedItem(attributeName);
                String nodeAttrValue = nodeAttr.getTextContent();
                if (append && nodeAttrValue != null && nodeAttrValue.length() > 0) {
                    nodeAttr.setTextContent(attributeValue + "_" + nodeAttrValue);
                } else {
                    nodeAttr.setTextContent(attributeValue);
                }
            }
        }

    }

}