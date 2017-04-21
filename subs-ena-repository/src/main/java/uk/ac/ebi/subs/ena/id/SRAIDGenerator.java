package uk.ac.ebi.subs.ena.id;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by neilg on 02/04/2017.
 */
public abstract class SRAIDGenerator implements IdentifierGenerator {
    private static final String ACCESSION_FROM_SEQUENCE_SQL = "select era.%s.nextval from dual";
    private static final String ACCESSION_FROM_PACKAGE_SQL = "select prefix_pkg.get_acc(?) from dual";

    private String sequenceName;
    private String prefix;
    private String format;

    public SRAIDGenerator(String sequenceName, String prefix, String format) {
        this.sequenceName = sequenceName;
        this.prefix = prefix;
        this.format = format;
    }

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        Connection connection = session.connection();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection
                    .prepareStatement(String.format(ACCESSION_FROM_SEQUENCE_SQL,sequenceName));

            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return String.format(format, prefix, resultSet.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                resultSet.close();
                ps.close();
            } catch (SQLException e) {
            }

        }
        return null;
    }
}
