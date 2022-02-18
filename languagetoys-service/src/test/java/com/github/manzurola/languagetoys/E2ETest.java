package com.github.manzurola.languagetoys;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.manzurola.languagetoys.service.app.App;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = App.class)
public class E2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void contextLoads() {

    }

    @Test
    public void givenCorrectAnswer_scoreIs100() {
        String request = createRequest(
            "Students are happy.",
            "A student is happy.",
            "A student is happy."
        ).toString();
        String expectedResponse = createResponse(1.0).toString();
        ResponseEntity<String> responseEntity =
            restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/grammar/annotate",
                request,
                String.class
            );
        String actualResponse = responseEntity.getBody();
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    public ObjectNode createRequest(
        String source,
        String target,
        String answer
    ) {
        return mapper
            .createObjectNode()
            .<ObjectNode>set("question", mapper.createObjectNode()
                .put("source", source)
                .put("target", target)
            )
            .put("answer", answer);
    }

    public ObjectNode createResponse(
        Double score
    ) {
        return mapper
            .createObjectNode().put("score", score);
    }
}
