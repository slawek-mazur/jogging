package io.stricte.jogging.service;

import io.stricte.jogging.domain.Run;
import io.stricte.jogging.domain.User;
import io.stricte.jogging.repository.RunRepository;
import io.stricte.jogging.repository.UserRepository;
import io.stricte.jogging.web.rest.model.RunDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
class RunServiceImpl implements RunService {

    private final RunRepository runRepository;

    private final UserRepository userRepository;

    @Autowired
    public RunServiceImpl(RunRepository runRepository, UserRepository userRepository) {
        this.runRepository = runRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Run create(RunDto runDto) {

        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        final User user = userRepository.findByEmail(principal.getName());

        final Run run = new Run();
        run.setUser(user);
        run.setDay(runDto.getDay());
        run.setDistance(runDto.getDistance());
        run.setDuration(runDto.getDuration());

        return runRepository.save(run);
    }

    public Page<Run> all(Pageable pageable, LocalDateTime from, LocalDateTime to) {
        return runRepository.findAllByCurrentUser(daySpecification(from, to), pageable);
    }

    private Specification<Run> daySpecification(LocalDateTime from, LocalDateTime to) {
        return (root, query, builder) -> {
            if (from != null && to != null) {
                return builder.greaterThanOrEqualTo(root.get("day"), from);
            } else if (from == null && to == null) {
                return builder.or();
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(root.get("day"), from);
            } else {
                return builder.lessThanOrEqualTo(root.get("day"), to);
            }
        };
    }

    @Override
    public Run one(int id) {
        return runRepository.findOneByCurrentUser(id);
    }

    @Override
    @Transactional
    public Run update(RunDto runDto) {

        final Run run = runRepository.findOneByCurrentUser(runDto.getId());
        run.setDay(runDto.getDay());
        run.setDistance(runDto.getDistance());
        run.setDuration(runDto.getDuration());

        return runRepository.save(run);
    }

    @Override
    public void delete(int id) {
        runRepository.delete(runRepository.findOneByCurrentUser(id));
    }
}
