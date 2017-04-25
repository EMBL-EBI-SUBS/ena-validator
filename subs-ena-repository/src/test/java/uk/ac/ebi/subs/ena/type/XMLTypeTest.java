package uk.ac.ebi.subs.ena.type;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.ena.ENATestRepositoryApplication;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.Assert.assertTrue;

/**
 * Created by neilg on 25/04/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ENATestRepositoryApplication.class)
public class XMLTypeTest {

    @Autowired
    DataSource dataSource;

    Connection connection;

    @Before
    public void setUp() throws Exception {
        connection = dataSource.getConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testGetConnection() throws Exception {
        assertTrue("connection is valid", connection.isValid(1000));
    }


}