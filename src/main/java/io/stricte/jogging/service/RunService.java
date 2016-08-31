package io.stricte.jogging.service;

import io.stricte.jogging.domain.Run;
import io.stricte.jogging.web.rest.model.RunDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RunService {

    Run create(RunDto runDto);

    Page<Run> all(Pageable pageable);

    Run one(int id);

    Run update(RunDto runDto);

    void delete(int id);
}
