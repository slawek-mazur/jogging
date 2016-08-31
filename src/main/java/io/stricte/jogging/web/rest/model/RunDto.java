package io.stricte.jogging.web.rest.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.stricte.jogging.domain.Distance;
import io.stricte.jogging.domain.util.DistanceSerializer;
import io.stricte.jogging.domain.util.DurationSerializer;
import io.stricte.jogging.domain.util.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RunDto {

    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime day;

    @NotNull
    @JsonSerialize(using = DistanceSerializer.class)
    private Distance distance;

    @NotNull
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;
}
