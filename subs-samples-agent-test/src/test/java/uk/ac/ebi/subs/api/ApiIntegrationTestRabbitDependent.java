package uk.ac.ebi.subs.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.ApiApplication;
import uk.ac.ebi.subs.RabbitMQDependentTest;
import uk.ac.ebi.subs.SamplesAgentApplication;
import uk.ac.ebi.subs.repository.repos.SubmissionRepository;
import uk.ac.ebi.subs.repository.repos.status.SubmissionStatusRepository;
import uk.ac.ebi.subs.repository.repos.submittables.SampleRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApiApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Category(RabbitMQDependentTest.class)
@EnableAutoConfiguration
@ActiveProfiles("test")
public class ApiIntegrationTestRabbitDependent {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @LocalServerPort
    private int port;
    private String rootUri;



    private ApiIntegrationTestHelper testHelper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    SubmissionStatusRepository submissionStatusRepository;

    @Autowired
    private SampleRepository sampleRepository;

    //@Autowired
    //RabbitAdmin admin;

    @Before
    public void buildUp() throws URISyntaxException {

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

        testHelper = new ApiIntegrationTestHelper(objectMapper,rootUri);
    }

    @After
    public void tearDown() throws IOException {
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Unirest.shutdown();
        submissionRepository.deleteAll();
        sampleRepository.deleteAll();
        submissionStatusRepository.deleteAll();
    }

    /**
     * create a submission with some samples and submit it
     *
     * @throws IOException
     * @throws UnirestException
     */
    @Test
    @Category(RabbitMQDependentTest.class)
    public void postSampleToAgent() throws IOException, UnirestException {
        Map<String, String> rootRels = testHelper.rootRels();

        String submissionLocation = testHelper.submissionWithSamples(rootRels);

        HttpResponse<JsonNode> submissionGetResponse = Unirest
                .get(submissionLocation)
                .headers(testHelper.getGetHeaders())
                .asJson();

        assertThat(submissionGetResponse.getStatus(), is(equalTo(HttpStatus.OK.value())));
        JSONObject payload = submissionGetResponse.getBody().getObject();

        Map<String,String> rels = testHelper.relsFromPayload(payload);

        assertThat(rels.get("submissionStatus"),notNullValue());
        String submissionStatusLocation = rels.get("submissionStatus");

        HttpResponse<JsonNode> submissionStatusGetResponse = Unirest
                .get(submissionStatusLocation)
                .headers(testHelper.getGetHeaders())
                .asJson();

        assertThat(submissionStatusGetResponse.getStatus(), is(equalTo(HttpStatus.OK.value())));
        JSONObject statusPayload = submissionStatusGetResponse.getBody().getObject();

        rels = testHelper.relsFromPayload(statusPayload);

        assertThat(rels.get("self"),notNullValue());
        submissionStatusLocation = rels.get("self");


        //update the submission
        //create a new submission
        HttpResponse<JsonNode> submissionPatchResponse = Unirest.patch(submissionStatusLocation)
                .headers(testHelper.getPostHeaders())
                .body("{\"status\": \"Submitted\"}")
                .asJson();

        assertThat(submissionPatchResponse.getStatus(), is(equalTo(HttpStatus.OK.value())));
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
