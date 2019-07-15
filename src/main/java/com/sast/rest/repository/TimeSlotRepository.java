package com.sast.rest.repository;

import com.sast.rest.domain.TimeSlot;
import com.sast.rest.domain.enumeration.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for the TimeSlot entity.
 */

@SuppressWarnings("unused")
@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByStatusAndDateBetweenAndPersonIdIn(Status status, LocalDate from, LocalDate to, List<Long> ids);

    @Query(value = "SELECT s.* from time_slots s WHERE s.date BETWEEN " +
            " NOW()\\:\\:DATE-EXTRACT(DOW FROM NOW())\\:\\:INTEGER + 3 " +
            " AND NOW()\\:\\:DATE-EXTRACT(DOW from NOW())\\:\\:INTEGER + 7 " +
            " AND status = 'FREE' " +
            " AND s.person_id IN :peopleIds ", nativeQuery = true)
    List<TimeSlot> getFreeTimeSlots(@Param("peopleIds") List<Long> peopleIds);
}
