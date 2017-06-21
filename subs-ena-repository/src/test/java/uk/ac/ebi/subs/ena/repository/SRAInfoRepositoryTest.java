package uk.ac.ebi.subs.ena.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.subs.ena.ENATestRepositoryApplication;
import uk.ac.ebi.subs.ena.data.SRAInfo;
import uk.ac.ebi.subs.ena.data.SubmissionStatus;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

/**
 * Created by neilg on 26/04/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ENATestRepositoryApplication.class)
@Transactional
public abstract class SRAInfoRepositoryTest<E extends SRAInfo, T extends SRARepository<E>>
        implements SubmittableTestObjectFactory<E> {

    static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = null;

    static String SUBMISSION_ACCOUNT_ID = "Webin-2";

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


    @Test
    public void findByAliasAndSubmissionAccountId() throws Exception {
        final SRAInfo sraInfo = repository.findByAliasAndSubmissionAccountId(getAlias(), SUBMISSION_ACCOUNT_ID);
        assertNotNull(sraInfo);
    }

    @Test
    public void save() throws Exception {
        final E submittable = createSubmittable(UUID.randomUUID().toString(), "Webin-2", SubmissionStatus.PRIVATE);
        if (submittable != null) {
            final E save = repository.save(submittable);
            assertNotNull(save.getId());
        }
    }

    abstract void setSubmissionRepository (T repository);

}