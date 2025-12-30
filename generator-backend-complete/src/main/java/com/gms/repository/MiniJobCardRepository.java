package com.gms.repository;
import com.gms.entity.MiniJobCard;
import com.gms.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MiniJobCardRepository extends JpaRepository<MiniJobCard, Long> {
    Page<MiniJobCard> findByEmployeeId(Long employeeId, Pageable pageable);
    Page<MiniJobCard> findByEmployeeIdAndStatus(Long employeeId, JobStatus status, Pageable pageable);
    Page<MiniJobCard> findByStatus(JobStatus status, Pageable pageable);
    List<MiniJobCard> findByMainTicketId(Long mainTicketId);
    List<MiniJobCard> findByEmployeeIdAndCreatedAtBetween(Long employeeId, LocalDateTime start, LocalDateTime end);
    List<MiniJobCard> findByStatusAndApproved(JobStatus status, Boolean approved);
}
