package com.gms.repository;

import com.gms.entity.SubTicket;
import com.gms.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubTicketRepository extends JpaRepository<SubTicket, Long> {

    @Query("SELECT st FROM SubTicket st " +
           "LEFT JOIN FETCH st.mainTicket " +
           "LEFT JOIN FETCH st.employee " +
           "WHERE st.mainTicket.id = :mainTicketId")
    List<SubTicket> findByMainTicketId(@Param("mainTicketId") Long mainTicketId);

    @Query("SELECT st FROM SubTicket st " +
           "LEFT JOIN FETCH st.mainTicket mt " +
           "LEFT JOIN FETCH mt.generator " +
           "LEFT JOIN FETCH st.employee " +
           "WHERE st.employee.id = :employeeId")
    List<SubTicket> findByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT st FROM SubTicket st " +
           "LEFT JOIN FETCH st.mainTicket " +
           "LEFT JOIN FETCH st.employee " +
           "WHERE st.id = :id")
    Optional<SubTicket> findByIdWithDetails(@Param("id") Long id);

    boolean existsByTicketNumber(String ticketNumber);

    List<SubTicket> findByMainTicketIdAndStatus(Long mainTicketId, TicketStatus status);

    @Query("SELECT COUNT(st) FROM SubTicket st WHERE st.mainTicket.id = :mainTicketId AND st.status = :status")
    long countByMainTicketIdAndStatus(@Param("mainTicketId") Long mainTicketId, @Param("status") TicketStatus status);

    @Query("SELECT st FROM SubTicket st " +
           "LEFT JOIN FETCH st.mainTicket mt " +
           "LEFT JOIN FETCH mt.generator " +
           "LEFT JOIN FETCH st.employee " +
           "WHERE st.status = :status")
    List<SubTicket> findByStatus(@Param("status") TicketStatus status);
}
