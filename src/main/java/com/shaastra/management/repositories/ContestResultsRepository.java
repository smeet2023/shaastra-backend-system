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

    // Custom query for ranking – ordering by score (descending) and then rank
    @Query("SELECT cr FROM ContestResults cr WHERE cr.contest.contestId = :contestId ORDER BY cr.score DESC, cr.rank_in_this_contest ASC")
    List<ContestResults> findRankingByContestId(@Param("contestId") Integer contestId);
    
    @Query("SELECT cr.score FROM ContestResults cr WHERE cr.contest.contestId = :contestId AND cr.contestParticipant.participant_id = :participantId")
    Optional<Integer> findScoreByContestIdAndParticipantId(@Param("contestId") Integer contestId,
                                                           @Param("participantId") Integer participantId);
}
