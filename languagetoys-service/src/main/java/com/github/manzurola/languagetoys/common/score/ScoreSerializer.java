package com.github.manzurola.languagetoys.common.score;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ScoreSerializer extends JsonSerializer<Score> {

    @Override
    public void serialize(
        Score id,
        JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider
    ) throws IOException {
        jsonGenerator.writeObject(id.value());
    }
}
