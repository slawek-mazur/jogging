package io.stricte.jogging.domain.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Duration;

public class DurationDeserializer extends JsonDeserializer<Duration> {

    private static final long serialVersionUID = 1L;

    public static final JsonDeserializer<? extends Duration> INSTANCE = new DurationDeserializer();

    @Override
    public Duration deserialize(JsonParser jp, DeserializationContext ctx)
        throws IOException {

        final long minutesAndSeconds = (long) (jp.getDoubleValue() * 100);

        final double secondsFromMinutes = (minutesAndSeconds / 100) * 60 + (minutesAndSeconds % 100);

        return Duration.ofSeconds((long) secondsFromMinutes);
    }
}
