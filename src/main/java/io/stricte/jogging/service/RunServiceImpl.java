package io.stricte.jogging.service;

import io.stricte.jogging.domain.Run;
import io.stricte.jogging.repository.RunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class RunServiceImpl implements RunService {

    private final RunRepository runRepository;

    @Autowired
    public RunServiceImpl(RunRepository runRepository) {
        this.runRepository = runRepository;
    }

    public Page<Run> getRunsForPrincipal(Principal principal, Pageable pageable) {
        return runRepository.findAll(pageable);
    }
}
