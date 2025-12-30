package com.gms.repository;
import com.gms.entity.EmployeeDayLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeDayLogRepository extends JpaRepository<EmployeeDayLog, Long> {
    Optional<EmployeeDayLog> findByEmployeeIdAndDayDate(Long employeeId, LocalDate dayDate);
    List<EmployeeDayLog> findByEmployeeIdAndDayDateBetween(Long employeeId, LocalDate start, LocalDate end);
}
