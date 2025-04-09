package com.shaastra.management.handler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shaastra.management.resource_representation.ContestsResrep;

public interface ContestsService {
    List<ContestsResrep> getAll();
    ContestsResrep getById(Integer id);
    ContestsResrep create(ContestsResrep resrep);
    ContestsResrep update(Integer id, ContestsResrep resrep);
    ContestsResrep partialUpdate(Integer id, Map<String, Object> updates);
    void delete(Integer id);
    public Map<String, Integer> getRecentParticipationSummary();
    Page<ContestsResrep> getUpcomingContests(OffsetDateTime dateTime, Pageable pageable);
}
