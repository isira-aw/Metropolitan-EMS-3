package com.gms.repository;
import com.gms.entity.JobStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobStatusLogRepository extends JpaRepository<JobStatusLog, Long> {
    List<JobStatusLog> findByMiniJobCardIdOrderByLoggedAtAsc(Long miniJobCardId);
}
