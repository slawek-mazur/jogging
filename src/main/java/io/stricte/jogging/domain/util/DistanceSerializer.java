package io.stricte.jogging.domain.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.stricte.jogging.domain.Distance;

import java.io.IOException;

public class DistanceSerializer extends JsonSerializer<Distance> {

    private static final long serialVersionUID = 1L;

    public static final JsonSerializer<Distance> INSTANCE = new DistanceSerializer();

    @Override
    public void serialize(Distance o, JsonGenerator generator, SerializerProvider provider)
        throws IOException {

        generator.writeNumber(o.toMeters());
    }
}
