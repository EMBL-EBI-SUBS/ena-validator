package uk.ac.ebi.subs.ena.processor;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.Marshaller;
import org.w3c.dom.Document;
import uk.ac.ebi.embl.api.validation.Origin;
import uk.ac.ebi.embl.api.validation.ValidationMessage;
import uk.ac.ebi.embl.api.validation.ValidationResult;
import uk.ac.ebi.ena.authentication.model.AuthResult;
import uk.ac.ebi.ena.sra.SRALoader;
import uk.ac.ebi.ena.sra.xml.SUBMISSIONSETDocument;
import uk.ac.ebi.ena.sra.xml.SubmissionType;
import uk.ac.ebi.subs.data.submittable.ENASubmittable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neilg on 12/04/2017.
 */
public abstract class AbstractSRALoaderService<T extends ENASubmittable> implements SRALoaderService<T> {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    SRALoader sraLoader;
    AuthResult authResult;
    XmlOptions xmlOptions = new XmlOptions();
    List<XmlError> validationErrorList;
    String schema;
    Marshaller marshaller;
    static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    static DocumentBuilder documentBuilder;
    static TransformerFactory transformerFactory;

    static {
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            transformerFactory = TransformerFactory.newInstance();
        } catch (ParserConfigurationException e) {
        }
    }

    public AbstractSRALoaderService(String principal,
                                    String loginName,
                                    String schema,
                                    Marshaller marshaller) {
        authResult = new AuthResult();
        authResult.setLoginName(loginName);
        authResult.setPrinciple(principal);
        authResult.setRoles(createAuthMap());
        sraLoader = new SRALoader();
        sraLoader.setTransactionMode(SRALoader.TransactionMode.NOT_TRANSACTIONAL);
        this.schema = schema;
        this.marshaller = marshaller;
    }

    private static Map<String, Boolean> createAuthMap() {
        Map<String, Boolean> roles = new HashMap<>();
        roles.put("EGA", false);
        roles.put("SUPER_USER", false);
        return roles;
    }

    @Override
    public ValidationResult getValidationResult() {
        ValidationResult validationResult = sraLoader.getValidationResult();
        return sraLoader.getValidationResult();
    }

    protected void logValidationErrors() {
        for (ValidationMessage<Origin> validationMessage : getValidationResult().getMessages())
            logger.error(validationMessage.getMessage());
    }

    @Override
    public void executeSRALoader(ENASubmittable enaSubmittable, String submissionAlias, Connection connection) throws Exception {
        final String submissionXML = createSubmissionXML(enaSubmittable, submissionAlias);
        Document document = documentBuilder.newDocument();
        marshaller.marshal(enaSubmittable,new DOMResult(document));
        String submittableXML = getDocumentString(document);
        final String accession = executeSRALoader(submissionXML, submittableXML, connection);
        enaSubmittable.setAccession(accession);
    }

    private String createSubmissionXML(ENASubmittable enaSubmittable, String submissionAlias) {
        final SUBMISSIONSETDocument submissionsetDocument = SUBMISSIONSETDocument.Factory.newInstance();
        final SubmissionType submissionType = submissionsetDocument.addNewSUBMISSIONSET().addNewSUBMISSION();
        submissionType.setCenterName(enaSubmittable.getTeam().getName());
        submissionType.setAlias(submissionAlias);
        createActions(submissionType,isUpdate(enaSubmittable),getSchema());
        return submissionsetDocument.xmlText();
    }

    /**
     * Creates the submission actions in the submissions XML
     *
     * @param submissionType
     * @param isUpdate
     * @param schema
     * @return
     */
    SubmissionType.ACTIONS createActions(SubmissionType submissionType, boolean isUpdate, String schema) {
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
        return actions;
    }

    private boolean isUpdate (ENASubmittable enaSubmittable) {
        if (enaSubmittable.getAccession() != null && !enaSubmittable.getAccession().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    abstract String getSchema ();

    String getDocumentString (Document document) throws TransformerException {
        DOMSource domSource = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }
}
