package io.stricte.jogging.repository;

import io.stricte.jogging.domain.Run;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RunRepository extends JpaRepository<Run, Integer> {

    @Query("select run from Run run where run.user.email = ?#{principal?.username}")
    Page<Run> findByCurrentUser(Pageable pageable);
}
