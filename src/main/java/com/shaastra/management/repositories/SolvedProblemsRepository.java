package com.shaastra.management.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shaastra.management.entities.SolvedProblems;

@Repository
public interface SolvedProblemsRepository extends JpaRepository<SolvedProblems, Integer> {
    // Retrieve solved problems by participant ID
    @Query("SELECT sp FROM SolvedProblems sp WHERE sp.contestParticipant.student.sh_id = :participantId")
    List<SolvedProblems> findByParticipantId(@Param("participantId") String participantId);
    
    // Retrieve solved problems for a specific contest and participant
    @Query("SELECT sp FROM SolvedProblems sp WHERE sp.contest.contestId = :contestId AND sp.contestParticipant.student.sh_id = :participantId")
    List<SolvedProblems> findByContestIdAndParticipantId(@Param("contestId") Integer contestId, @Param("participantId") String participantId);
    
    // Calculate total score in a contest for a participant
    @Query("SELECT COALESCE(SUM(sp.score), 0) FROM SolvedProblems sp WHERE sp.contest.contestId = :contestId AND sp.contestParticipant.student.sh_id = :participantId")
    Integer findTotalScoreByContestIdAndParticipantId(@Param("contestId") Integer contestId, @Param("participantId") String participantId);
}
