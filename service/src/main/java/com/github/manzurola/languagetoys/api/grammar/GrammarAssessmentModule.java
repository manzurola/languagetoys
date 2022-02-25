package com.github.manzurola.languagetoys.api.grammar;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.manzurola.errant4j.core.Annotator;
import com.github.manzurola.errant4j.core.Errant;
import com.github.manzurola.spacy4j.adapters.corenlp.CoreNLPAdapter;
import com.github.manzurola.spacy4j.api.SpaCy;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Configuration
public class GrammarAssessmentModule {

    @Bean
    public Annotator annotator() {
        SpaCy spaCy = SpaCy.create(CoreNLPAdapter.forEnglish());
        return Errant.forEnglish(spaCy);
    }

    @Bean
    public GrammarAssessmentService grammarAssessmentService(Annotator annotator) {
        return new GrammarAssessmentService(
            annotator,
            new PenaltyScoringStrategy()
        );
    }

    @Bean
    public GrammarAssessmentController grammarAssessmentController(
        GrammarAssessmentService service
    ) {
        return new GrammarAssessmentController(service);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder
            .serializationInclusion(JsonInclude.Include.NON_ABSENT);
    }
}
