package com.gms.repository;
import com.gms.entity.TicketAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketAssignmentRepository extends JpaRepository<TicketAssignment, Long> {
    List<TicketAssignment> findByMainTicketId(Long mainTicketId);
    List<TicketAssignment> findByEmployeeId(Long employeeId);
}
