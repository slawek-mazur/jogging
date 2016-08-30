package io.stricte.jogging.domain.util;

import io.stricte.jogging.domain.Distance;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

public class PersistenceConverters {

    @Converter(autoApply = true)
    public static class DistanceConverter implements AttributeConverter<Distance, Long> {

        @Override
        public Long convertToDatabaseColumn(Distance distance) {
            return distance.toMeters();
        }

        @Override
        public Distance convertToEntityAttribute(Long meters) {
            return Distance.ofMeters(meters);
        }
    }
}
