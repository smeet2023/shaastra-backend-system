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

    // Custom method to fetch participants based on a contest
    @Query("SELECT cp FROM ContestParticipants cp JOIN cp.contests c WHERE c.contestId = :contestId")
    List<ContestParticipants> findByContestId(@Param("contestId") Integer contestId);
    
    @Query("SELECT cp FROM ContestParticipants cp JOIN cp.student s WHERE s.sh_id = :id")
    Optional<ContestParticipants> findById(@Param("id") String id);
}
