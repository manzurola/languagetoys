package com.github.manzurola.languagetoys.app;

import com.github.manzurola.languagetoys.modules.grammar.GrammarAssessmentModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@EnableAutoConfiguration
@Import({ GrammarAssessmentModule.class })
public class App {

    public static void main(String[] args) {
        System.out.printf("XXX App main started XXX");
        SpringApplication app = new SpringApplication(
            App.class
        );
        ConfigurableApplicationContext run = app.run(args);
        System.out.println(List.of(run.getBeanDefinitionNames()));
    }

}
