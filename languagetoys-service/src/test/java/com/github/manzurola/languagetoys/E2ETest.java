package com.github.manzurola.languagetoys;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.manzurola.languagetoys.app.App;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = App.class)
public class E2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper mapper = new ObjectMapper();
    private String url;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        this.url = "http://localhost:" + port + "/api/v1/grammar/assess";
        System.out.println(url);
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void contextLoads() {

    }

    @Test
    public void givenCorrectAnswer_scoreIs100() throws JsonProcessingException {
        JsonNode body = createRequest(
            "Students are happy.",
            "A student is happy.",
            "A student is happy."
        );

        HttpEntity<String> request =
            new HttpEntity<>(body.toString(), headers);

        String actualResponse = restTemplate.postForObject(
            url,
            request,
            String.class
        );

        JsonNode root = mapper.readTree(actualResponse);
        double actualScore = root.get("score").asDouble();
        Assertions.assertEquals(1.0, actualScore);
    }

    public ObjectNode createRequest(
        String source,
        String target,
        String input
    ) {
        return mapper
            .createObjectNode()
            .put("source", source)
            .put("target", target)
            .put("input", input);
    }

    public ObjectNode createResponse(
        Double score
    ) {
        return mapper
            .createObjectNode()
            .put("score", score);
    }
}
