package uk.ac.ebi.subs.ena.processor;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.slf4j.Logger;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.oxm.Marshaller;
import org.w3c.dom.Document;

//import uk.ac.ebi.ena.authentication.model.AuthResult;
//import uk.ac.ebi.ena.sra.SRALoader;
//import uk.ac.ebi.ena.sra.xml.SUBMISSIONSETDocument;
//import uk.ac.ebi.ena.sra.xml.SubmissionType;
import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.ValidationMessage;
import uk.ac.ebi.ena.authentication.model.AuthResult;
import uk.ac.ebi.ena.sra.SRALoader;
import uk.ac.ebi.ena.sra.StudyInfo;
import uk.ac.ebi.ena.sra.xml.SUBMISSIONSETDocument;
import uk.ac.ebi.ena.sra.xml.StudyType;
import uk.ac.ebi.ena.sra.xml.SubmissionType;
import uk.ac.ebi.subs.data.FullSubmission;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.Term;
import uk.ac.ebi.subs.data.status.ProcessingStatus;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class ENAAgentProcessor<T extends Submittable> extends AgentProcessor<T> {
    static final Logger logger = LoggerFactory.getLogger(ENAAgentProcessor.class);
    DocumentBuilderFactory documentBuilderFactory = null;
    Marshaller marshaller;
    Document newProcessedDocument;
    Document updatedProcessedDocument;
    TransformerFactory transformerFactory = null;
    Connection connection = null;
    AuthResult authResult;
    XmlOptions xmlOptions = new XmlOptions();
    List<XmlError> validationErrors;
    String submissionAccountId;
    SRALoader.TransactionMode transactionMode = SRALoader.TransactionMode.TRANSACTIONAL_WITH_ROLLBACK;
    SRALoader sraLoader;


    public ENAAgentProcessor(SubmissionEnvelope submissionEnvelope, Archive archive, Marshaller marshaller, Connection connection, String submissionAccountId, SRALoader.TransactionMode transactionMode) {
        super(submissionEnvelope, archive);
        this.marshaller = marshaller;
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        transformerFactory = TransformerFactory.newInstance();
        this.connection = connection;
        authResult = new AuthResult();
        authResult.setPrinciple(submissionAccountId);
        authResult.setLoginName(submissionAccountId);
        authResult.setRoles(createAuthMap());
        this.transactionMode = transactionMode;
        sraLoader = new SRALoader();
        sraLoader.setTransactionMode(transactionMode);

    }

    @Override
    protected ProcessingStatus loadData(T submittable, SubmissionEnvelope submissionEnvelope) throws Exception {
        String studyStringXML = getSubmitableString(submittable);
        String submissionXML = getSubmissionXML(submittable, submissionEnvelope);
        String accession = executeSRALoader(submissionXML,studyStringXML);
        submittable.setAccession(accession);
        return new ProcessingStatus(ProcessingStatusEnum.Accepted);
    }

    @Override
    protected ProcessingStatus updateData(T submittable, SubmissionEnvelope submissionEnvelope) throws Exception {
        String studyStringXML = getSubmitableString(submittable);
        String submissionXML = getSubmissionXML(submittable, submissionEnvelope);
        executeSRALoader(submissionXML,studyStringXML);
        return new ProcessingStatus(ProcessingStatusEnum.Accepted);
    }

    private String getSubmissionXML(T submittable, SubmissionEnvelope submissionEnvelope) {
        final SUBMISSIONSETDocument submissionsetDocument = SUBMISSIONSETDocument.Factory.newInstance();
        final SubmissionType submissionType = submissionsetDocument.addNewSUBMISSIONSET().addNewSUBMISSION();
        submissionType.setCenterName(submissionEnvelope.getSubmission().getTeam().getName());
        submissionType.setAlias(submittable.getId());
        addActions(submissionType,submittable);
        return submissionsetDocument.xmlText();
    }

    protected void addActions(SubmissionType submissionType, T submittable) {
        addAddUpdateAction(submissionType, isUpdate(submittable), getSchemaName());
    }

    private Map<String, Boolean> createAuthMap() {
        Map<String, Boolean> roles = new HashMap<>();
        roles.put("EGA",false);
        roles.put("SUPER_USER",false);
        return roles;
    }

    protected Document marshalAndTransform (T submittable) throws ParserConfigurationException, IOException, TransformerException, URISyntaxException {
        Document document = documentBuilderFactory.newDocumentBuilder().newDocument();
        DOMResult domResult = new DOMResult(document);
        marshaller.marshal(submittable,domResult);
        String documentString = getDocumentString(document);
        logger.info(documentString);
        return document;
    }

    abstract Transformer getTransformer () throws URISyntaxException, TransformerConfigurationException;

    Transformer getTransformer(String transformerResource) throws URISyntaxException, TransformerConfigurationException {
        final URL resource = getClass().getResource(transformerResource);
        java.io.File xsltFile = new File(resource.toURI());
        StreamSource stylesource = new StreamSource(xsltFile);
        return transformerFactory.newTransformer(stylesource);
    }

    private Document transformDocument(Transformer transformer, Document inputDocument) throws TransformerException, ParserConfigurationException {
        Document documentResult = documentBuilderFactory.newDocumentBuilder().newDocument();
        DOMSource domSource = new DOMSource(inputDocument);
        DOMResult domResult = new DOMResult(documentResult);
        transformer.transform(domSource, domResult);
        return documentResult;
    }

    String getDocumentString (Document document) throws TransformerException {
        DOMSource domSource = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }

    static void addAddUpdateAction (SubmissionType submissionType, boolean isUpdate, String schema) {
        final SubmissionType.ACTIONS actions = submissionType.addNewACTIONS();
        final SubmissionType.ACTIONS.ACTION action = actions.addNewACTION();
        if (isUpdate) {
            SubmissionType.ACTIONS.ACTION.MODIFY modify = action.addNewMODIFY();
            modify.setSchema(uk.ac.ebi.ena.sra.xml.SubmissionType.ACTIONS.ACTION.MODIFY.Schema.Enum.forString(schema));
        } else {
            SubmissionType.ACTIONS.ACTION.ADD add = action.addNewADD();
            add.setSchema(uk.ac.ebi.ena.sra.xml.SubmissionType.ACTIONS.ACTION.ADD.Schema.Enum.forString(schema));
            add.setSource(schema + "xml");
        }
    }

    protected void resetXMLValidator () {
        validationErrors = new ArrayList<>();
        xmlOptions.setErrorListener(validationErrors);
    }

    protected boolean validateXMLObject (XmlObject xmlObject) {
        resetXMLValidator();
        boolean validationStatus = xmlObject.validate(xmlOptions);
        if (!validationStatus) {
            logger.error("Error validating " + xmlObject);
            validationErrors.forEach(xmlError -> logger.error(xmlError.getMessage()));
            return false;
        }
        return true;
    }

    abstract String getSchemaName ();

    public static void injectXMLSchema (Document document, String xsdURL) {
        final Element firstElement = (Element)document.getFirstChild();
        firstElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                "xsi:noNamespaceSchemaLocation", xsdURL);
    }

    protected String getSubmitableString(T submittable) throws Exception {
        final Document document = marshalAndTransform(submittable);
        final XmlObject xmlObject = XmlObject.Factory.parse(document);
        if (!validateXMLObject(xmlObject)) {
            new ProcessingStatus(ProcessingStatusEnum.Error);
        }

        return getDocumentString(document);
    }

    String executeSRALoader (String submissionXML, String submittableXML) throws Exception {
        throw new SRALoaderAccessionException(submissionXML,submittableXML);
    }

    protected void logValidationErrors () {
        for (ValidationMessage<Origin> validationMessage : sraLoader.getValidationResult().getMessages())
            logger.error(validationMessage.getMessage());
    }
}
