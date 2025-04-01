package com.shaastra.management.handler;

import java.util.List;
import java.util.Map;

import com.shaastra.management.resource_representation.ContestProblemResrep;

public interface ContestProblemService {
    List<ContestProblemResrep> getAll();
    ContestProblemResrep getById(Integer id);
    ContestProblemResrep create(ContestProblemResrep resrep);
    ContestProblemResrep update(Integer id, ContestProblemResrep resrep);
    ContestProblemResrep partialUpdate(Integer id, Map<String, Object> updates);
    void delete(Integer id);
    List<ContestProblemResrep> searchByDifficulty(String difficulty);
}
