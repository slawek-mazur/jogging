package io.stricte.jogging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.stricte.jogging.domain.Distance;
import io.stricte.jogging.domain.util.DistanceDeserializer;
import io.stricte.jogging.domain.util.DistanceSerializer;
import io.stricte.jogging.domain.util.DurationDeserializer;
import io.stricte.jogging.domain.util.DurationSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@Configuration
public class JacksonConfiguration {

    public static final DateTimeFormatter ISO_FIXED_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    public static final DateTimeFormatter ISO_DATE_OPTIONAL_TIME =
        new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .optionalStart()
            .appendLiteral('T')
            .append(DateTimeFormatter.ISO_TIME)
            .toFormatter();

    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        builder.serializerByType(Distance.class, DistanceSerializer.INSTANCE);
        builder.deserializerByType(Distance.class, DistanceDeserializer.INSTANCE);

        builder.serializerByType(Duration.class, DurationSerializer.INSTANCE);
        builder.deserializerByType(Duration.class, DurationDeserializer.INSTANCE);

        return builder.createXmlMapper(false).build();
    }

}
