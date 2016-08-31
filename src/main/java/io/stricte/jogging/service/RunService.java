package io.stricte.jogging.service;

import io.stricte.jogging.domain.Run;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RunService {

    Page<Run> currentUserRuns(Pageable pageable);

    Run currentUserRun(int id);
}
