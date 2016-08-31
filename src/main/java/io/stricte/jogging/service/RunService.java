package io.stricte.jogging.service;

import io.stricte.jogging.domain.Run;
import io.stricte.jogging.web.rest.model.RunDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RunService {

    Page<Run> currentUserRuns(Pageable pageable);

    Run currentUserRun(int id);

    Run createRun(RunDto runDto);

    Run updateRun(RunDto runDto);

    void deleteRun(int id);
}
