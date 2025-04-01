package com.shaastra.management.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shaastra.management.entities.ContestResults;

@Repository
public interface ContestResultsRepository extends JpaRepository<ContestResults, Integer> {
    // Get ranking for a contest ordered by score and rank
    @Query("SELECT cr FROM ContestResults cr WHERE cr.contest.contestId = :contestId ORDER BY cr.score DESC, cr.rank_in_this_contest ASC")
    List<ContestResults> findRankingByContestId(@Param("contestId") Integer contestId);
    
    // Fetch participant's score in a specific contest
    @Query("SELECT cr.score FROM ContestResults cr WHERE cr.contest.contestId = :contestId AND cr.contestParticipant.student.sh_id = :participantId")
    Optional<Integer> findScoreByContestIdAndParticipantId(@Param("contestId") Integer contestId, @Param("participantId") String participantId);

}
