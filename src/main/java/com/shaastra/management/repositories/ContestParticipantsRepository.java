package com.shaastra.management.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shaastra.management.entities.ContestParticipants;

@Repository
public interface ContestParticipantsRepository extends JpaRepository<ContestParticipants, Integer> {
    // Fetch participants for a specific contest
    @Query("SELECT cp FROM ContestParticipants cp JOIN cp.contests c WHERE c.contestId = :contestId")
    List<ContestParticipants> findByContestId(@Param("contestId") Integer contestId);

    // Find participant by their student ID
    @Query("SELECT cp FROM ContestParticipants cp WHERE cp.student.sh_id = :shId")
    Optional<ContestParticipants> findByShId(@Param("shId") String shId);


}

