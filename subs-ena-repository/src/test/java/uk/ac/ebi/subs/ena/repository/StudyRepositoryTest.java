package uk.ac.ebi.subs.ena.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.ena.ENATestRepositoryApplication;
import uk.ac.ebi.subs.ena.data.Study;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by neilg on 25/04/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ENATestRepositoryApplication.class)
public class StudyRepositoryTest {

    @Autowired
    StudyRepository studyRepository;

    @Test
    public void find() throws Exception {
        final Study study = studyRepository.findOne("SRP000576");
        assertNotNull(study);
    }

    @Test
    public void findBySubmissionId() throws Exception {
        final List<Study> studyList = studyRepository.findBySubmissionId("ERA000001");
        assertFalse(studyList.isEmpty());
    }

    @Test
    public void findByAliasAndSubmissionAccountId() throws Exception {
        final Study study = studyRepository.findByAliasAndSubmissionAccountId("Salmonella_Typhi_HTS","Webin-2");
        assertNotNull(study);
    }



}