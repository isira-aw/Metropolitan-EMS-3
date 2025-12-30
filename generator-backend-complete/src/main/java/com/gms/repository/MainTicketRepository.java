package com.gms.repository;
import com.gms.entity.MainTicket;
import com.gms.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface MainTicketRepository extends JpaRepository<MainTicket, Long> {

    @Query("SELECT t FROM MainTicket t " +
           "LEFT JOIN FETCH t.generator " +
           "LEFT JOIN FETCH t.createdBy " +
           "WHERE t.id = :id")
    Optional<MainTicket> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT t FROM MainTicket t " +
           "LEFT JOIN FETCH t.generator " +
           "LEFT JOIN FETCH t.createdBy " +
           "WHERE t.status = :status")
    Page<MainTicket> findByStatus(@Param("status") TicketStatus status, Pageable pageable);

    @Query("SELECT t FROM MainTicket t " +
           "LEFT JOIN FETCH t.generator " +
           "LEFT JOIN FETCH t.createdBy " +
           "WHERE t.scheduledDate = :date")
    Page<MainTicket> findByScheduledDate(@Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT t FROM MainTicket t " +
           "LEFT JOIN FETCH t.generator " +
           "LEFT JOIN FETCH t.createdBy")
    Page<MainTicket> findAllWithDetails(Pageable pageable);

    boolean existsByTicketNumber(String ticketNumber);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(t.ticketNumber, 13) AS int)), 0) FROM MainTicket t WHERE t.ticketNumber LIKE :prefix%")
    Integer findMaxTicketNumberForPrefix(@Param("prefix") String prefix);
}
