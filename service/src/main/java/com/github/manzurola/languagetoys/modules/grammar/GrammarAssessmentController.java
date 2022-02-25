package com.github.manzurola.languagetoys.modules.grammar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/grammar/assess")
public class GrammarAssessmentController {

    private static final Logger logger = LoggerFactory.getLogger(
        GrammarAssessmentController.class);

    private final GrammarAssessmentService service;

    public GrammarAssessmentController(GrammarAssessmentService service) {
        this.service = service;
    }

    @PostMapping(
        consumes = "application/json",
        produces = "application/json"
    )
    public @ResponseBody
    GrammarAssessmentResponse assess(
        @RequestBody GrammarAssessmentRequest request
    ) {
        logger.info("request {}", request);
        return service.assess(request);
    }
}
