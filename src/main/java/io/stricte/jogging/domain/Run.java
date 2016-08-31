package io.stricte.jogging.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.stricte.jogging.domain.util.DistanceSerializer;
import io.stricte.jogging.domain.util.DurationSerializer;
import io.stricte.jogging.domain.util.LocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Entity
public class Run implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    @Column(name = "day", nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime day;

    @NotNull
    @Column(name = "distance", nullable = false)
    @JsonSerialize(using = DistanceSerializer.class)
    private Distance distance;

    @NotNull
    @Column(name = "duration", nullable = false)
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;

    @ManyToOne
    @JoinColumn(name = "user")
    @JsonBackReference
    private User user;

    public double averageSpeed() {
        return distance.toMeters() / (double) duration.toMillis();
    }
}
