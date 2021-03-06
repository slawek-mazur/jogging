package io.stricte.jogging.domain.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.stricte.jogging.domain.Distance;

import java.io.IOException;

public class DistanceDeserializer extends JsonDeserializer<Distance> {

    private static final long serialVersionUID = 1L;

    public static final JsonDeserializer<? extends Distance> INSTANCE = new DistanceDeserializer();

    @Override
    public Distance deserialize(JsonParser jp, DeserializationContext ctx)
        throws IOException {

        return Distance.ofMeters(jp.getLongValue());
    }
}
