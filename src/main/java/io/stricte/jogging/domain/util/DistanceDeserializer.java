package io.stricte.jogging.domain.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.stricte.jogging.domain.Distance;

import java.io.IOException;

public class DistanceDeserializer extends JsonDeserializer<Distance> {

    public static final JsonDeserializer<? extends Distance> INSTANCE = new DistanceDeserializer();

    @Override
    public Distance deserialize(JsonParser jp, DeserializationContext ctx)
        throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);
        Number meters = node.get("meters").numberValue();

        return Distance.ofMeters(meters.longValue());
    }
}
