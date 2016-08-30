package io.stricte.jogging.service;

import io.stricte.jogging.domain.Run;
import io.stricte.jogging.repository.RunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collection;

@Service
public class RunServiceImpl implements RunService {

    private final RunRepository runRepository;

    @Autowired
    public RunServiceImpl(RunRepository runRepository) {
        this.runRepository = runRepository;
    }

    public Collection<Run> getRunsForPrincipal(Principal principal) {
        return runRepository.findAll();
    }
}
