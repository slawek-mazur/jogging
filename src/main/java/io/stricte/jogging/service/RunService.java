package io.stricte.jogging.service;

import io.stricte.jogging.domain.Run;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

public interface RunService {

    Page<Run> getRunsForPrincipal(Principal principal, Pageable pageable);
}
