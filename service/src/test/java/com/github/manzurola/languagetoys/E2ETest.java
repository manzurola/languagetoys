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
              "scoredAnswer" : {
                "score" : 1.0,
                "edits" : [ {
                  "source" : [ {
                    "text" : "A",
                    "spaceAfter" : " "
                  } ],
                  "target" : [ {
                    "text" : "A",
                    "spaceAfter" : " "
                  } ],
                  "operation" : "equal"
                }, {
                  "source" : [ {
                    "text" : "student",
                    "spaceAfter" : " "
                  } ],
                  "target" : [ {
                    "text" : "student",
                    "spaceAfter" : " "
                  } ],
                  "operation" : "equal"
                }, {
                  "source" : [ {
                    "text" : "is",
                    "spaceAfter" : " "
                  } ],
                  "target" : [ {
                    "text" : "is",
                    "spaceAfter" : " "
                  } ],
                  "operation" : "equal"
                }, {
                  "source" : [ {
                    "text" : "happy",
                    "spaceAfter" : ""
                  } ],
                  "target" : [ {
                    "text" : "happy",
                    "spaceAfter" : ""
                  } ],
                  "operation" : "equal"
                }, {
                  "source" : [ {
                    "text" : ".",
                    "spaceAfter" : ""
                  } ],
                  "target" : [ {
                    "text" : ".",
                    "spaceAfter" : ""
                  } ],
                  "operation" : "equal"
                } ]
              }
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
    public void givenIncorrectAnswer_properErrorsAreShown() throws
                                                            JsonProcessingException {
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
              "scoredAnswer" : {
                "score" : -0.040000000000000036,
                "edits" : [ {
                  "source" : [ {
                    "text" : "a",
                    "spaceAfter" : " "
                  } ],
                  "target" : [ {
                    "text" : "A",
                    "spaceAfter" : " "
                  } ],
                  "operation" : "substitute",
                  "error" : {
                    "id" : "replacement-orthography",
                    "type" : "replacement",
                    "category" : "orthography"
                  }
                }, {
                  "source" : [ {
                    "text" : "students",
                    "spaceAfter" : " "
                  }, {
                    "text" : "are",
                    "spaceAfter" : " "
                  } ],
                  "target" : [ {
                    "text" : "student",
                    "spaceAfter" : " "
                  }, {
                    "text" : "is",
                    "spaceAfter" : " "
                  } ],
                  "operation" : "substitute",
                  "error" : {
                    "id" : "replacement-other",
                    "type" : "replacement",
                    "category" : "other"
                  }
                }, {
                  "source" : [ {
                    "text" : "happy",
                    "spaceAfter" : ""
                  } ],
                  "target" : [ {
                    "text" : "happy",
                    "spaceAfter" : ""
                  } ],
                  "operation" : "equal"
                }, {
                  "source" : [ ],
                  "target" : [ {
                    "text" : ".",
                    "spaceAfter" : ""
                  } ],
                  "operation" : "insert",
                  "error" : {
                    "id" : "missing-punctuation",
                    "type" : "missing",
                    "category" : "punctuation"
                  }
                } ]
              }
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
