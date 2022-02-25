package com.github.manzurola.languagetoys;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public void givenCorrectAnswer_scoreIs1() throws JsonProcessingException {
        String request = """
            {
            	"question": {
            		"source": "Students are happy.",
            		"target": "A student is happy."
            	},
            	"answer": {
            		"input": "A student is happy."
            	}
            }
            """;

        String expectedResponse = """
            {
              "score": 1.0,
              "words": [
                {
                  "source": {
                    "text": "A",
                    "spaceAfter": " "
                  },
                  "target": {
                    "text": "A",
                    "spaceAfter": " "
                  },
                  "correction": "NONE",
                  "error": "E:OTHER"
                },
                {
                  "source": {
                    "text": "student",
                    "spaceAfter": " "
                  },
                  "target": {
                    "text": "student",
                    "spaceAfter": " "
                  },
                  "correction": "NONE",
                  "error": "E:OTHER"
                },
                {
                  "source": {
                    "text": "is",
                    "spaceAfter": " "
                  },
                  "target": {
                    "text": "is",
                    "spaceAfter": " "
                  },
                  "correction": "NONE",
                  "error": "E:OTHER"
                },
                {
                  "source": {
                    "text": "happy",
                    "spaceAfter": ""
                  },
                  "target": {
                    "text": "happy",
                    "spaceAfter": ""
                  },
                  "correction": "NONE",
                  "error": "E:OTHER"
                },
                {
                  "source": {
                    "text": ".",
                    "spaceAfter": ""
                  },
                  "target": {
                    "text": ".",
                    "spaceAfter": ""
                  },
                  "correction": "NONE",
                  "error": "E:OTHER"
                }
              ]
            }
            """;

        HttpEntity<String> httpRequest =
            new HttpEntity<>(request, headers);

        String actualResponse = restTemplate.postForObject(
            url,
            httpRequest,
            String.class
        );

        Assertions.assertEquals(
            mapper.readTree(expectedResponse).toPrettyString(),
            mapper.readTree(actualResponse).toPrettyString()
        );
    }

    @Test
    public void givenIncorrectAnswer_properErrorsAreShown() throws JsonProcessingException {
        String request = """
            {
            	"question": {
            		"source": "Students are happy.",
            		"target": "A student is happy."
            	},
            	"answer": {
            		"input": "a students are happy"
            	}
            }
            """;

        String expectedResponse = """
            {
              "score" : -0.040000000000000036,
              "words" : [ {
                "source" : {
                  "text" : "a",
                  "spaceAfter" : " "
                },
                "target" : {
                  "text" : "A",
                  "spaceAfter" : " "
                },
                "correction" : "SUBSTITUTE",
                "error" : "R:ORTHOGRAPHY"
              }, {
                "source" : {
                  "text" : "students are",
                  "spaceAfter" : " "
                },
                "target" : {
                  "text" : "student is",
                  "spaceAfter" : " "
                },
                "correction" : "SUBSTITUTE",
                "error" : "R:OTHER"
              }, {
                "source" : {
                  "text" : "happy",
                  "spaceAfter" : ""
                },
                "target" : {
                  "text" : "happy",
                  "spaceAfter" : ""
                },
                "correction" : "NONE",
                "error" : "E:OTHER"
              }, {
                "source" : {
                  "text" : "",
                  "spaceAfter" : ""
                },
                "target" : {
                  "text" : ".",
                  "spaceAfter" : ""
                },
                "correction" : "INSERT",
                "error" : "M:PUNCTUATION"
              } ]
            }
            """;

        HttpEntity<String> httpRequest =
            new HttpEntity<>(request, headers);

        String actualResponse = restTemplate.postForObject(
            url,
            httpRequest,
            String.class
        );

        Assertions.assertEquals(
            mapper.readTree(expectedResponse).toPrettyString(),
            mapper.readTree(actualResponse).toPrettyString()
        );
    }

}
