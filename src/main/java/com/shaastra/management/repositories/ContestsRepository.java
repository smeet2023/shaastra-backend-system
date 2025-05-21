package com.shaastra.management.repositories;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shaastra.management.entities.Contests;

import jakarta.transaction.Transactional;

@Repository
public interface ContestsRepository extends JpaRepository<Contests, Integer> {
    @Query("SELECT c FROM Contests c WHERE c.contest_date > :dateTime")
    Page<Contests> findByContestDateAfter(@Param("dateTime") OffsetDateTime dateTime, Pageable pageable);
    @Query(value = "SELECT * FROM contests ORDER BY contest_date DESC LIMIT 1", nativeQuery = true)
    Optional<Contests> findTopByOrderByContestDateDesc();
    List<Contests> findByStatus(String status);
    @Modifying
    @Transactional
    @Query(
      value = "INSERT INTO contest_contest_participant_join_table " +
              "(contest_id, participant_id) " +
              "VALUES (:contestId, :participantId)",
      nativeQuery = true
    )
    void addParticipantToContest(
        @Param("contestId")       Integer contestId,
        @Param("participantId")   Integer participantId
    );

}
