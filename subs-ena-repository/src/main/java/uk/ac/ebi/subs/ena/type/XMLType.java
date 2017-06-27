package uk.ac.ebi.subs.ena.type;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

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
import java.io.Serializable;
import java.io.Writer;
import java.sql.*;

/**
 * Created by neilg on 02/04/2017.
 */
public class XMLType implements UserType{

    private static final Logger log = LoggerFactory.getLogger(XMLType.class);
    static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    static TransformerFactory transformerFactory = TransformerFactory.newInstance();
    static DocumentBuilder documentBuilder = null;

    static {
        try {
            documentBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.error("Error initialising XMLType", e);
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[] {Types.SQLXML};
    }

    @Override
    public Class returnedClass() {
        return Document.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        } else {
            if (x != null && y != null) {
                Document document1 = (Document)x;
                document1.normalizeDocument();
                Document document2 = (Document)y;
                document2.normalizeDocument();
                return (document1.isEqualNode(document2));
            }
        }
        return false;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        SQLXML xmlType = rs.getSQLXML(names[0]);
        Document document = null;
        try {
            if (xmlType != null) {
                document = documentBuilder.parse(new InputSource(xmlType.getCharacterStream()));
            } else {
                document = documentBuilder.newDocument();
            }
        } catch (Exception e) {
            log.error("Exception in nullSafeGet",e);
            throw new SQLException(e.getMessage());
        } finally {
            xmlType.free();
        }
        return document;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.SQLXML,"SYS.XMLTYPE");
        } else {
            final SQLXML sqlxml = st.getConnection().createSQLXML();
            final Writer writer = sqlxml.setCharacterStream();
            Document document = (Document) value;
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(writer);
            try {
                final Transformer transformer = transformerFactory.newTransformer();
                transformer.transform(domSource,streamResult);
                st.setSQLXML(index,sqlxml);
                writer.close();
            } catch (Exception e) {
                throw new SQLException(e.getMessage());
            } finally {
                sqlxml.free();
            }
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        Document documentCopy = null;
        try {
            final Transformer transformer = transformerFactory.newTransformer();
            documentCopy = documentBuilder.newDocument();
            DOMResult domResult = new DOMResult(documentCopy);
            DOMSource domSource = new DOMSource((Document)value);
            transformer.transform(domSource,domResult);
        } catch (TransformerConfigurationException e) {
            new HibernateException(e.getMessage());
        } catch (TransformerException e) {
            new HibernateException(e.getMessage());
        }

        return documentCopy;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return null;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return null;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return null;
    }
}
