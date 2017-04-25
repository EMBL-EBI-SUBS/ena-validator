package uk.ac.ebi.subs.ena.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.ena.ENATestRepositoryApplication;
import uk.ac.ebi.subs.ena.data.Sample;
import uk.ac.ebi.subs.ena.data.Study;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by neilg on 25/04/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ENATestRepositoryApplication.class)
public class SampleRepositoryTest {

    @Autowired
    SampleRepository sampleRepository;

    @Test
    public void find() throws Exception {
        final Sample sample = sampleRepository.findOne("ERS000007");
        assertNotNull(sample);
    }

    @Test
    public void findBySubmissionId() throws Exception {
        final List<Sample> sampleList = sampleRepository.findBySubmissionId("ERA000001");
        assertFalse(sampleList.isEmpty());
    }

    @Test
    public void findByAliasAndSubmissionAccountId() throws Exception {
        final Sample sample = sampleRepository.findByAliasAndSubmissionAccountId("Solexa sequencing of Saccharomyces paradoxus strain Y55 random 200 bp library","Webin-2");
        assertNotNull(sample);
    }



}