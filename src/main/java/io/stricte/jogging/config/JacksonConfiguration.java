package io.stricte.jogging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.stricte.jogging.domain.Distance;
import io.stricte.jogging.domain.util.DistanceDeserializer;
import io.stricte.jogging.domain.util.DistanceSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.annotation.PostConstruct;
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

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    //To be replaced by a Jackson2ObjectMapperBuilderCustomizer in Spring-boot 1.4
    @PostConstruct
    public void postConstruct() {
        //this.builder.serializers(new ZonedDateTimeSerializer(ISO_FIXED_FORMAT));
        //Will not be needed anymore with SB 1.4 (Jackson > 2.7.1)
        //this.builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(ISO_DATE_OPTIONAL_TIME));
        this.builder.serializerByType(Distance.class, DistanceSerializer.INSTANCE);
        this.builder.deserializerByType(Distance.class, DistanceDeserializer.INSTANCE);
    }

    @Bean
    public ObjectMapper jacksonObjectMapper() {
        return this.builder.createXmlMapper(false).build();
    }

}
