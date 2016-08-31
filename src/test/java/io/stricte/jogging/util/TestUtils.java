package io.stricte.jogging.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.stricte.jogging.domain.Distance;
import io.stricte.jogging.domain.util.DistanceDeserializer;
import io.stricte.jogging.domain.util.DistanceSerializer;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static io.stricte.jogging.config.JacksonConfiguration.ISO_DATE_OPTIONAL_TIME;
import static io.stricte.jogging.config.JacksonConfiguration.ISO_FIXED_FORMAT;

public class TestUtils {

    public static byte[] convertObjectToJsonBytes(Object object)
        throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(ISO_FIXED_FORMAT));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(ISO_DATE_OPTIONAL_TIME));

        module.addSerializer(Duration.class, DurationSerializer.INSTANCE);
        module.addDeserializer(Duration.class, DurationDeserializer.INSTANCE);

        module.addSerializer(Distance.class, DistanceSerializer.INSTANCE);
        module.addDeserializer(Distance.class, DistanceDeserializer.INSTANCE);

        mapper.registerModule(module);

        return mapper.writeValueAsBytes(object);
    }
}
