package com.gms.repository;
import com.gms.entity.MainTicket;
import com.gms.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface MainTicketRepository extends JpaRepository<MainTicket, Long> {
    Page<MainTicket> findByStatus(TicketStatus status, Pageable pageable);
    Page<MainTicket> findByScheduledDate(LocalDate date, Pageable pageable);
    boolean existsByTicketNumber(String ticketNumber);
}
