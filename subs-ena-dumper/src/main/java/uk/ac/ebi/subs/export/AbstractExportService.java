package uk.ac.ebi.subs.export;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.w3c.dom.Node;
import uk.ac.ebi.subs.data.client.PartOfSubmission;
import uk.ac.ebi.subs.data.submittable.ENASubmittable;
import uk.ac.ebi.subs.data.submittable.MappingHelper;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.ena.data.SRAInfo;
import uk.ac.ebi.subs.ena.data.SubmittableSRAInfo;
import uk.ac.ebi.subs.ena.repository.SampleRepository;
import uk.ac.ebi.subs.ena.repository.SubmittableSRARepository;
import uk.ac.ebi.subs.stresstest.ClientCompleteSubmission;

import javax.persistence.criteria.CriteriaBuilder;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractExportService<K extends Submittable,V extends ENASubmittable<K>> implements ExportService {
    public static final int PAGE_SIZE = 10000;
    SubmittableSRARepository<? extends SubmittableSRAInfo> submittableSRARepository;
    Class<V> enaSubmittableClass;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    SampleRepository sampleRepository;
    Unmarshaller unmarshaller;
    static TransformerFactory transFactory = TransformerFactory.newInstance();
    XPathFactory xPathFactory = XPathFactory.newInstance();
    XPath xPath = null;
    XPathExpression xPathExpression = null;
    ObjectMapper objectMapper;
    public AbstractExportService(SubmittableSRARepository<? extends SubmittableSRAInfo> submittableSRARepository, Class<V> enaSubmittableClass, String enaMarshaller, String rootNodeXpathExpression, ObjectMapper objectMapper) {
        this.submittableSRARepository = submittableSRARepository;
        this.enaSubmittableClass = enaSubmittableClass;
        this.objectMapper = objectMapper;
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            unmarshaller = MappingHelper.createUnmarshaller(enaSubmittableClass, MappingHelper.SUBMITTABLE_PACKAGE, enaMarshaller, MappingHelper.COMPONENT_PACKAGE, MappingHelper.ATTRIBUTE_MAPPING);
            xPath = xPathFactory.newXPath();
            xPathExpression = xPath.compile(rootNodeXpathExpression);
        } catch (Exception e) {
            logger.info("Exception in createUnmarshaller",  e);
        }
    }

    public void export(Path path, String submissionAccountId) {
        logger.info("Dumping in " + path.toString());
        Pageable page = new PageRequest(0, PAGE_SIZE);
        long rowCount = submittableSRARepository.countBySubmissionAccountIdAndStatusId(submissionAccountId,4);

        for (int i = 0; i * PAGE_SIZE < rowCount; i++) {
            final List<? extends SubmittableSRAInfo> submittableList = submittableSRARepository.findBySubmissionAccountIdAndStatusId(submissionAccountId, 4, page);

            for (SubmittableSRAInfo submittableSRAInfo : submittableList) {
                try {
                    Path resolve = getPathForDate(path, submittableSRAInfo);
                    writeSubmittableSRAInfo(submittableSRAInfo, resolve);
                }  catch (Exception e) {
                    logger.info("Error in running sampleXPathExpression",e);
                }

            }
            logger.info("dumped " + page.getPageSize() + " records in page " + page.getPageNumber());
            page = page.next();
        }

        logger.info("Dumped " + rowCount + " objects");
    }

    @Override
    public void export(ClientCompleteSubmission clientCompleteSubmission, String submissionId) {
        final List<? extends SubmittableSRAInfo> submittableList = submittableSRARepository.findBySubmissionId(submissionId);

        for (SubmittableSRAInfo submittableSRAInfo : submittableList) {
            try {
                final K submittable = getSubmittable(submittableSRAInfo);
                submittable.setAccession(null);
                updateClientCompleteSubmission(clientCompleteSubmission,submittable);
            }  catch (Exception e) {
                logger.info("Error in dumping submission " + submissionId,e);
            }
        }

    }

    @Override
    public void exportBySubmissionId(Path path, String submissionId) {
        final List<? extends SubmittableSRAInfo> submittableList = submittableSRARepository.findBySubmissionId(submissionId);
        final Path submissionPath = path.resolve(submissionId);

        for (SubmittableSRAInfo submittableSRAInfo : submittableList) {
            try {
                writeSubmittableSRAInfo(submittableSRAInfo, submissionPath);
            }  catch (Exception e) {
                logger.info("Error in dumping submission " + submissionId,e);
            }
        }

    }

    protected abstract void updateClientCompleteSubmission(ClientCompleteSubmission clientCompleteSubmission, K submittable);

    static Path getPathForDate(Path path, SRAInfo sraInfo) {
        final LocalDateTime localDateTime = sraInfo.getFirstCreated().toLocalDateTime();
        return path.resolve(Integer.toString(localDateTime.getYear())).resolve(localDateTime.getMonth().name()).resolve(Integer.toString(localDateTime.getDayOfMonth()));
    }

    private void writeSubmittableSRAInfo(SubmittableSRAInfo submittableSRAInfo, Path resolve) throws XPathExpressionException, JAXBException, IllegalAccessException, IOException {
        final K submittable = getSubmittable(submittableSRAInfo);
        writeSubmittableJSON(resolve, submittable);
    }

    private void writeSubmittableJSON(Path resolve, K submittable) throws IOException {
        Files.createDirectories(resolve);
        final Path exportPath = resolve.resolve(submittable.getAccession() + ".json");
        submittable.setAccession(null);
        objectMapper.writeValue(exportPath.toFile(),submittable);
        logger.trace("Dumped " + submittable.getAccession());
    }

    protected K getSubmittable(SubmittableSRAInfo submittable) throws XPathExpressionException, JAXBException, IllegalAccessException {
        Node node = submittable.getDocument();
        node = (Node) xPathExpression.evaluate(node, XPathConstants.NODE);
        final V enaSubmittable = unmarshaller.unmarshal(node, enaSubmittableClass).getValue();
        enaSubmittable.deSerialiseAttributes();
        return enaSubmittable.getBaseObject();
    }

    public static String getDocumentString(Node node) throws TransformerException {
        DOMSource source = new DOMSource(node);
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter);
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(source, result);
        return stringWriter.toString();
    }

    public Class<? extends ENASubmittable> getEnaSubmittableClass() {
        return enaSubmittableClass;
    }

    public XPathExpression getxPathExpression() {
        return xPathExpression;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
