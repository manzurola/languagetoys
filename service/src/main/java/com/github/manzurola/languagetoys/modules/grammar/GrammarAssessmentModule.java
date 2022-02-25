package com.github.manzurola.languagetoys.modules.grammar;

import com.github.manzurola.errant4j.core.Annotator;
import com.github.manzurola.errant4j.core.Errant;
import com.github.manzurola.spacy4j.adapters.corenlp.CoreNLPAdapter;
import com.github.manzurola.spacy4j.api.SpaCy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
