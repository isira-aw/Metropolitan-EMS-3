package com.gms.repository;
import com.gms.entity.EmployeeScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeScoreRepository extends JpaRepository<EmployeeScore, Long> {
    Page<EmployeeScore> findByEmployeeId(Long employeeId, Pageable pageable);
}
