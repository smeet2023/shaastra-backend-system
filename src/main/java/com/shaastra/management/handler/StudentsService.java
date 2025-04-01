package com.shaastra.management.handler;

import java.util.List;
import java.util.Map;

import com.shaastra.management.resource_representation.ContestsResrep;
import com.shaastra.management.resource_representation.StudentsResrep;

public interface StudentsService {
    List<StudentsResrep> getAll();
    StudentsResrep getByErpId(long erpId);
    StudentsResrep create(StudentsResrep resrep);
    StudentsResrep update(long erpId, StudentsResrep resrep);
    StudentsResrep partialUpdate(long erpId, Map<String, Object> updates);
    void delete(long erpId);
    List<ContestsResrep> getContestHistory(long erpId);
}
