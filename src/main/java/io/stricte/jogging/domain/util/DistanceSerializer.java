package io.stricte.jogging.domain.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.stricte.jogging.domain.Distance;

import java.io.IOException;

public class DistanceSerializer extends JsonSerializer<Distance> {

    @Override
    public void serialize(Distance distance, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
        throws IOException {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("meters", distance.toMeters());
        jsonGenerator.writeEndObject();
    }
}
