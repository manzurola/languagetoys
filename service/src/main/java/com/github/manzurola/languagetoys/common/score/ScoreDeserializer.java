package com.github.manzurola.languagetoys.common.score;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ScoreDeserializer extends JsonDeserializer<Score> {

    @Override
    public Score deserialize(
        JsonParser jsonParser,
        DeserializationContext deserializationContext
    ) throws IOException, JsonProcessingException {
        return Score.of(jsonParser.getDoubleValue());
    }
}
