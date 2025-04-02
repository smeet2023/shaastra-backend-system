package com.shaastra.management.handler;

import java.util.List;
import java.util.Map;

import com.shaastra.management.resource_representation.ContestResultPostResRep;
import com.shaastra.management.resource_representation.ContestResultsResrep;

public interface ContestResultsService {
    List<ContestResultsResrep> getAll();
    ContestResultsResrep getById(Integer id);
    ContestResultsResrep create(ContestResultPostResRep resrep);
    ContestResultsResrep update(Integer id, ContestResultsResrep resrep);
    ContestResultsResrep partialUpdate(Integer id, Map<String, Object> updates);
    void delete(Integer id);
    List<ContestResultsResrep> getRankingForContest(Integer contestId);
    Integer getScoreForContestAndParticipant(Integer contestId, String participantId);

}
