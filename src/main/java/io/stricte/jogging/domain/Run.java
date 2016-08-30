package io.stricte.jogging.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
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
    @Min(value = 1)
    @Column(name = "distance", nullable = false)
    private Distance distance;

    @NotNull
    @Min(value = 1)
    @Column(name = "duration", nullable = false)
    private Duration duration;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
}
