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

        final String[] minutesAndSeconds = jp.getText().split("\\.");

        final int minutesAsSeconds = Integer.parseInt(minutesAndSeconds[0]) * 60;
        final int secondsAsSeconds = Integer.parseInt(minutesAndSeconds[1]) * 10;

        return Duration.ofSeconds(minutesAsSeconds + secondsAsSeconds);
    }
}
