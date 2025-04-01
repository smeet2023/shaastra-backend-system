package com.shaastra.management.handler;

import java.util.List;
import java.util.Map;

import com.shaastra.management.resource_representation.ContestsResrep;

public interface ContestsService {
    List<ContestsResrep> getAll();
    ContestsResrep getById(Integer id);
    ContestsResrep create(ContestsResrep resrep);
    ContestsResrep update(Integer id, ContestsResrep resrep);
    ContestsResrep partialUpdate(Integer id, Map<String, Object> updates);
    void delete(Integer id);
    List<ContestsResrep> getUpcomingContests();
}
