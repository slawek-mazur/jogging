package io.stricte.jogging.repository;

import io.stricte.jogging.domain.Run;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RunRepository
    extends JpaRepository<Run, Integer>, JpaSpecificationExecutor {

    @Query("select run from Run run where run.user.email like " +
        "?#{hasRole('ROLE_ADMIN') ? '%' : principal?.username}")
    Page<Run> findAllByCurrentUser(Specification specification, Pageable pageable);

    @Query("select run from Run run where run.user.email like " +
        "?#{hasRole('ROLE_ADMIN') ? '%' : principal?.username} and run.id = :id")
    Run findOneByCurrentUser(@Param("id") int id);
}
