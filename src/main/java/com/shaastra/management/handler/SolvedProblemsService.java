package com.shaastra.management.handler;

import java.util.List;
import java.util.Map;

import com.shaastra.management.resource_representation.SolvedProblemsResrep;

public interface SolvedProblemsService {
    List<SolvedProblemsResrep> getAll();
    SolvedProblemsResrep getById(Integer id);
    List<SolvedProblemsResrep> create(List<SolvedProblemsResrep> resrep);
    SolvedProblemsResrep update(Integer id, SolvedProblemsResrep resrep);
    SolvedProblemsResrep partialUpdate(Integer id, Map<String, Object> updates);
    void delete(Integer id);
    List<SolvedProblemsResrep> getByContestAndParticipant(Integer contestId, Integer participantId);
    List<SolvedProblemsResrep> getByParticipantId(Integer participantId);
    Integer getTotalMarks(Integer contestId, Integer participantId);

}
