package uk.ac.ebi.subs.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.rabbitmq.client.AMQP;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.omg.PortableServer.THREAD_POLICY_ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.*;
import uk.ac.ebi.subs.ena.EnaAgentApplication;
import uk.ac.ebi.subs.ena.queue.RabbitQueueStats;
import uk.ac.ebi.subs.messaging.Queues;
import uk.ac.ebi.subs.repository.model.Submission;
import uk.ac.ebi.subs.repository.repos.SubmissionRepository;
import uk.ac.ebi.subs.repository.repos.status.SubmissionStatusRepository;
import uk.ac.ebi.subs.repository.repos.submittables.StudyRepository;
import uk.ac.ebi.subs.repository.repos.submittables.SampleRepository;
import uk.ac.ebi.subs.repository.repos.submittables.AssayRepository;
import uk.ac.ebi.subs.repository.repos.submittables.SampleRepository;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApiApplication.class, EnaAgentApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    private StudyRepository studyRepository;

    @Autowired
    private SampleRepository sampleRepository;

    @Autowired
    private AssayRepository assayRepository;

    @Autowired
    RabbitAdmin admin;

    @Autowired
    RabbitQueueStats rabbitQueueStats;

    @Before
    public void buildUp() throws URISyntaxException {

        rootUri = "http://localhost:" + port + "/api";
        submissionRepository.deleteAll();
        studyRepository.deleteAll();
        sampleRepository.deleteAll();
        submissionStatusRepository.deleteAll();
        assayRepository.deleteAll();

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
        int queueCount = rabbitQueueStats.getQueueSize(Queues.ENA_AGENT);
        Unirest.shutdown();
        submissionRepository.deleteAll();
        studyRepository.deleteAll();
        sampleRepository.deleteAll();
        submissionStatusRepository.deleteAll();
        assayRepository.deleteAll();
    }

    /**
     * create a submission with some samples and submit it
     *
     * @throws IOException
     * @throws UnirestException
     */
    @Test
    @Category(RabbitMQDependentTest.class)
    public void postStudyToAgent() throws IOException, UnirestException {
        Map<String, String> rootRels = testHelper.rootRels();

        String submissionLocation = testHelper.submissionWithStudies(rootRels);

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
