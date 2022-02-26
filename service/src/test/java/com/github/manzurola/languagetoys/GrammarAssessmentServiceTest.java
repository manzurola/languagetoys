package com.github.manzurola.languagetoys;

import com.github.manzurola.errant4j.core.Errant;
import com.github.manzurola.languagetoys.api.grammar.*;
import com.github.manzurola.spacy4j.adapters.corenlp.CoreNLPAdapter;
import com.github.manzurola.spacy4j.api.SpaCy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GrammarAssessmentServiceTest {
    private static GrammarAssessmentService service;

    @BeforeAll
    static void beforeAll() {
        service =
            new GrammarAssessmentService(
                Errant.forEnglish(SpaCy.create(
                    CoreNLPAdapter.forEnglish())),
                new PenaltyScoringStrategy()
            );
    }

    @Test
    void givenErrorInImportantWord_andPenaltyStrategy_scoreIs0() {
        Question question = new Question(
            "A dog is cute.",
            "Dogs are cute"
        );
        Answer answer = new Answer("Dogs is cute");

        double expectedScore = 0.0;

        GrammarAssessmentResponse response =
            service.assess(new GrammarAssessmentRequest(
            question,
            answer
        ));

        double actualScore = response.scoredAnswer().score();

        assertEquals(expectedScore, actualScore);
    }

//    @Test
//    void givenErrorInPunctuation_andPenaltyStrategy_scoreIs99() {
//        Question question = new Question(
//            "A dog is cute.",
//            "Dogs are cute"
//        );
//        Answer answer = new Answer("Dogs are cute");
//
//        double expectedScore = 0.99;
//
//        GrammarAssessmentResponse response =
//            service.assess(new GrammarAssessmentRequest(
//                question,
//                answer
//            ));
//
//        double actualScore = response.scoredAnswer().score();
//
//        assertEquals(expectedScore, actualScore);
//    }
}
