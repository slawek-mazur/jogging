package io.stricte.jogging.web.rest.model;

import io.stricte.jogging.domain.Distance;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RunDto {

    @NotNull
    private LocalDateTime day;

    @NotNull
    private Distance distance;

    @NotNull
    private Duration duration;
}
