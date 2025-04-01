package com.shaastra.management.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shaastra.management.entities.SolvedProblems;

@Repository
public interface SolvedProblemsRepository extends JpaRepository<SolvedProblems, Integer> {

    // Custom method: retrieve solved problems by participant id
    @Query(value = "SELECT * FROM solved_problems WHERE contest_participant_id = :participantId", nativeQuery = true)
    List<SolvedProblems> findByContestParticipant_Participant_id(@Param("participantId") Integer participantId);
    
    @Query("SELECT sp FROM SolvedProblems sp WHERE sp.contest.contestId = :contestId AND sp.contestParticipant.participant_id = :participantId")
    List<SolvedProblems> findByContestIdAndParticipantId(@Param("contestId") Integer contestId, @Param("participantId") Integer participantId);

    @Query("SELECT COALESCE(SUM(sp.score), 0) FROM SolvedProblems sp " +
    	       "WHERE sp.contest.contestId = :contestId AND sp.contestParticipant.participant_id = :participantId")
    	Integer findTotalScoreByContestIdAndParticipantId(@Param("contestId") Integer contestId,
    	                                                  @Param("participantId") Integer participantId);


  }
