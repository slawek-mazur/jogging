package io.stricte.jogging.repository;

import io.stricte.jogging.domain.Run;
import io.stricte.jogging.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunRepository extends JpaRepository<Run, Integer> {

    Run findByUser(User user);
}
