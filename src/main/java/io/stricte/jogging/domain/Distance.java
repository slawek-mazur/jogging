package io.stricte.jogging.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.stricte.jogging.domain.util.DistanceSerializer;

import java.io.Serializable;

@JsonSerialize(using = DistanceSerializer.class)
public class Distance implements Serializable {

    private static final Distance ZERO = new Distance(0);

    private final long meters;

    private Distance(long meters) {
        this.meters = meters;
    }

    public static Distance ofMeters(long meters) {
        return create(meters);
    }

    private static Distance create(long meters) {
        if (meters <= 0) {
            return ZERO;
        }
        return new Distance(meters);
    }

    public long toMeters() {
        return meters;
    }
}
