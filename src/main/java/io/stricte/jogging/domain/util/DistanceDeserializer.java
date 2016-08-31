package io.stricte.jogging.domain.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.stricte.jogging.domain.Distance;

import java.io.IOException;

public class DistanceDeserializer extends JsonDeserializer<Distance> {

    public static final JsonDeserializer<? extends Distance> INSTANCE = new DistanceDeserializer();

    @Override
    public Distance deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException {
        
        return null;
    }
}
