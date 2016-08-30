package io.stricte.jogging.domain;

public class Distance {

    public static final Distance ZERO = new Distance(0);

    private final long meters;

    public Distance(long meters) {
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
