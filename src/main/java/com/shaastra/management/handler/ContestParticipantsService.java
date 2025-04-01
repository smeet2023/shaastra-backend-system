package com.shaastra.management.handler;

import java.util.List;

import com.shaastra.management.resource_representation.ContestParticipantsGetResrep;
import com.shaastra.management.resource_representation.ContestParticipantsPatchResrep;
import com.shaastra.management.resource_representation.ContestParticipantsResrep;

public interface ContestParticipantsService {
    List<ContestParticipantsGetResrep> getAll();
    ContestParticipantsGetResrep getById(String id);
    ContestParticipantsResrep create(ContestParticipantsResrep resrep);
    ContestParticipantsGetResrep patchUpdate(Integer id, ContestParticipantsPatchResrep patchResrep);
    void delete(Integer id);
    List<ContestParticipantsResrep> getByContestId(Integer contestId);
}
