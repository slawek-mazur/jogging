package io.stricte.jogging.service;

import io.stricte.jogging.domain.Run;

import java.security.Principal;
import java.util.Collection;

public interface RunService {
    
    Collection<Run> getRunsForPrincipal(Principal principal);
}
