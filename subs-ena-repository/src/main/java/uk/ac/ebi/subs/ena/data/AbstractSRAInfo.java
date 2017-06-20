package uk.ac.ebi.subs.ena.data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by neilg on 01/04/2017.
 */
@MappedSuperclass
public abstract class AbstractSRAInfo<T extends AbstractSRAInfo> implements SRAInfo {

    static TransformerFactory transformerFactory = TransformerFactory.newInstance();
    static Transformer transformer;

    static {
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
        }
    }

    @Column(name = "SUBMISSION_ACCOUNT_ID")
    String submissionAccountId;

    @Column(name = "VERSION")
    String version;

    @Column(name = "EGA_ID")
    String egaId;

    @Column(name = "FIRST_CREATED")
    Timestamp firstCreated;

    @Column(name = "MD5")
    String md5;

    public AbstractSRAInfo() {
        this.firstCreated = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    @Override
    public String getSubmissionAccountId() {
        return submissionAccountId;
    }

    @Override
    public void setSubmissionAccountId(String submissionAccountId) {
        this.submissionAccountId = submissionAccountId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEgaId() {
        return egaId;
    }

    public void setEgaId(String egaId) {
        this.egaId = egaId;
    }

    public Timestamp getFirstCreated() {
        return firstCreated;
    }

    public void setFirstCreated(Timestamp firstCreated) {
        this.firstCreated = firstCreated;
    }

    @Override
    public String getMD5() {
        return md5;
    }

    @Override
    public void setMD5(String md5) {
        this.md5 = md5;
    }

    void transformedDocumentResult (Result result) throws TransformerException {
        if (getDocument() != null) {
            DOMSource domSource = new DOMSource(getDocument());
            transformer.transform(domSource, result);
        }
    }

    public void updateMD5 () {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(byteArrayOutputStream);
            transformedDocumentResult(result);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5sum = messageDigest.digest(byteArrayOutputStream.toByteArray());
            this.md5 = String.format("%032X", new BigInteger(1, md5sum));
        } catch (NoSuchAlgorithmException nsae) {
        } catch (TransformerException e) {
        }
    }
}
