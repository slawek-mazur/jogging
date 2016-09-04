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

        final long time = o.getSeconds();

        final long minutes = time / 60;
        final long seconds = time - (minutes * 60);

        generator.writeString(String.format("%d.%02d", minutes, seconds));
    }
}
