package io.stricte.jogging.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private LocalDateTime day;

    @NotNull
    @Column(name = "distance", nullable = false)
    private Distance distance = Distance.ofMeters(0);

    @NotNull
    @Column(name = "duration", nullable = false)
    private Duration duration = Duration.ofMinutes(1);

    @ManyToOne
    @JoinColumn(name = "user")
    @JsonBackReference
    private User user;

    @Transient
    private double averageSpeed = 0.0;

    @PostLoad
    void afterLoad() {
        averageSpeed = (long) ((distance.toMeters() / (double) duration.toMinutes()) * 1e2) / 1e2;
    }
}