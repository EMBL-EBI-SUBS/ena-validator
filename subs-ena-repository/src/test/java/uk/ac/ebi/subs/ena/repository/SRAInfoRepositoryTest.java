package uk.ac.ebi.subs.ena.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.ena.ENATestRepositoryApplication;
import uk.ac.ebi.subs.ena.data.AbstractSRAInfo;
import uk.ac.ebi.subs.ena.data.SRAInfo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by neilg on 26/04/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ENATestRepositoryApplication.class)
public abstract class SRAInfoRepositoryTest<E extends SRAInfo, T extends SRARepository<E>>
        implements SubmittableTestObjectFactory<E> {

    static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = null;

    static String SUBMISSION_ACCOUNT_ID = "Webin-2";

    //@Autowired
    T repository;

    public SRAInfoRepositoryTest() {
    }

    @Before
    public void setUp() throws Exception {
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void find() throws Exception {
        final Object sraInfo = repository.findOne(getId());
        //final SRAInfo one = repository.findOne(getId());
        assertNotNull(sraInfo);
    }

    /*
    @Test
    public void findBySubmissionId() throws Exception {
        String id = getSubmissionId();
        final List<? extends AbstractSRAInfo> submittableList = repository.findBySubmissionId(id);
        assertFalse(submittableList.isEmpty());
    }
    */

    @Test
    public void findByAliasAndSubmissionAccountId() throws Exception {
        final SRAInfo sraInfo = repository.findByAliasAndSubmissionAccountId(getAlias(), SUBMISSION_ACCOUNT_ID);
        assertNotNull(sraInfo);
    }

    abstract T setSubmissionRepository (T repository);

}