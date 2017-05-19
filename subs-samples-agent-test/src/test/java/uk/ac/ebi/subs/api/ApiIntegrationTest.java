package uk.ac.ebi.subs.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.ApiApplication;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.client.Sample;
import uk.ac.ebi.subs.repository.model.SubmissionStatus;
import uk.ac.ebi.subs.repository.repos.SubmissionRepository;
import uk.ac.ebi.subs.repository.repos.status.SubmissionStatusRepository;
import uk.ac.ebi.subs.repository.repos.submittables.SampleRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApiApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ApiIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @LocalServerPort
    private int port;
    private String rootUri;



    private ApiIntegrationTestHelper testHelper;

    //@Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    SubmissionStatusRepository submissionStatusRepository;

    @Autowired
    private SampleRepository sampleRepository;


    @Before
    public void buildUp() throws URISyntaxException, UnirestException {

        rootUri = "http://localhost:" + port + "/api";
        submissionRepository.deleteAll();
        sampleRepository.deleteAll();
        submissionStatusRepository.deleteAll();

        Unirest.setObjectMapper(new com.mashape.unirest.http.ObjectMapper() {
            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return objectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return objectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        testHelper = createApiIntegrationTestHelper(objectMapper,rootUri);
    }

    ApiIntegrationTestHelper createApiIntegrationTestHelper(ObjectMapper objectMapper, String rootUri) throws UnirestException {
        return new ApiIntegrationTestHelper(objectMapper, rootUri);
    }


    @After
    public void tearDown() throws IOException {
        Unirest.shutdown();
        submissionRepository.deleteAll();
        sampleRepository.deleteAll();
        submissionStatusRepository.deleteAll();
    }

    //@Test
    public void checkRootRels() throws UnirestException, IOException {
        Map<String, String> rootRels = testHelper.rootRels();

        assertThat(rootRels.keySet(), hasItems("submissions:create", "samples:create"));
    }

    //@Test
    public void postSubmission() throws UnirestException, IOException {
        Map<String, String> rootRels = testHelper.rootRels();

        Submission submission = Helpers.generateSubmission();
        HttpResponse<JsonNode> submissionResponse = testHelper.postSubmission(rootRels, submission);

        List<SubmissionStatus> submissionStatuses = submissionStatusRepository.findAll();
        assertThat(submissionStatuses, notNullValue());
        assertThat(submissionStatuses, hasSize(1));
        SubmissionStatus submissionStatus = submissionStatuses.get(0);
        assertThat(submissionStatus.getStatus(), notNullValue());
        assertThat(submissionStatus.getStatus(), equalTo("Draft"));
    }



    //@Test
    public void submissionWithSamples() throws IOException, UnirestException {
        Map<String, String> rootRels = testHelper.rootRels();
        String submissionLocation = testHelper.submissionWithSamples(rootRels);
    }

    //@Test
    public void testPut() throws IOException, UnirestException {
        Map<String, String> rootRels = testHelper.rootRels();

        Submission submission = Helpers.generateSubmission();
        HttpResponse<JsonNode> submissionResponse = testHelper.postSubmission(rootRels, submission);

        String submissionLocation = submissionResponse.getHeaders().getFirst("Location");
        Map<String, String> submissionRels = testHelper.relsFromPayload(submissionResponse.getBody().getObject());

        assertThat(submissionRels.get("samples"), notNullValue());

        Sample sample = Helpers.generateTestClientSamples(1).get(0);
        //add samples to the submission

        sample.setSubmission(submissionLocation);

        HttpResponse<JsonNode> sampleResponse = Unirest.post(rootRels.get("samples:create"))
                .headers(testHelper.getPostHeaders())
                .body(sample)
                .asJson();

        assertThat(sampleResponse.getStatus(), is(equalTo(HttpStatus.CREATED.value())));
        assertThat(sampleResponse.getHeaders().getFirst("Location"), notNullValue());

        String sampleLocation = sampleResponse.getHeaders().getFirst("Location");

        sample.setAlias("bob"); //modify the sample
        sample.setSubmission(submissionLocation);

        HttpResponse<JsonNode> samplePutResponse = Unirest.put(sampleLocation)
                .headers(testHelper.getPostHeaders())
                .body(sample)
                .asJson();

        logger.info("samplePutResponse: {}", samplePutResponse.getBody());
        assertThat(samplePutResponse.getStatus(), is(equalTo(HttpStatus.OK.value())));


    }








}
