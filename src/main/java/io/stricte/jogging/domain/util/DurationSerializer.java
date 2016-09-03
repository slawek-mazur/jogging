package io.stricte.jogging.domain.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

public class DurationSerializer extends JsonSerializer<Duration> {

    private static final long serialVersionUID = 1L;

    public static final JsonSerializer<Duration> INSTANCE = new DurationSerializer();

    @Override
    public void serialize(Duration o, JsonGenerator generator, SerializerProvider provider)
        throws IOException {

        generator.writeString(o.toMinutes() + "." + o.getSeconds());
    }
}
