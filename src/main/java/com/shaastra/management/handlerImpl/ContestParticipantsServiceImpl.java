package com.shaastra.management.handlerImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shaastra.management.entities.ContestParticipants;
import com.shaastra.management.entities.Contests;
import com.shaastra.management.entities.Students;
import com.shaastra.management.exceptions.ResourceNotFoundException;
import com.shaastra.management.handler.ContestParticipantsService;
import com.shaastra.management.repositories.ContestParticipantsRepository;
import com.shaastra.management.repositories.ContestsRepository;
import com.shaastra.management.repositories.StudentsRepository;
import com.shaastra.management.resource_representation.ContestParticipantsGetResrep;
import com.shaastra.management.resource_representation.ContestParticipantsPatchResrep;
import com.shaastra.management.resource_representation.ContestParticipantsResrep;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContestParticipantsServiceImpl implements ContestParticipantsService {
    
    private final ContestParticipantsRepository repository;
    private final ModelMapper modelMapper; // Injected mapping utility
    private final StudentsRepository studentsRepository;
    private final ContestsRepository contestsRepository;
    private static final Logger logger = LoggerFactory.getLogger(ContestParticipantsServiceImpl.class);

    @Override
    public List<ContestParticipantsGetResrep> getAll() {
        List<ContestParticipants> participants = repository.findAll();
        return participants.stream()
            .map(cp -> {
                ContestParticipantsGetResrep resrep = ContestParticipantsGetResrep.builder()
                        .participant_id(cp.getParticipant_id())
                        .sh_id(cp.getStudent() != null ? cp.getStudent().getSh_id() : null)
                        .studentName(cp.getStudent() != null ? cp.getStudent().getName() : null) // assuming Student has getName()
                        .personal_email(cp.getStudent() != null ? cp.getStudent().getPersonal_email() : null)
                        .build();
                // Map associated contest IDs
                if (cp.getContests() != null && !cp.getContests().isEmpty()) {
                    Set<Integer> contestIds = cp.getContests().stream()
                        .map(Contests::getContestId)
                        .collect(Collectors.toSet());
                    resrep.setContestIds(contestIds);
                } else {
                    resrep.setContestIds(new HashSet<>());
                }
                return resrep;
            })
            .collect(Collectors.toList());
    }

    @Override
    public ContestParticipantsGetResrep patchUpdate(Integer participantId, ContestParticipantsPatchResrep patchDto) {
        // 1. Fetch the participant
        ContestParticipants cp = repository.findById(participantId)
            .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + participantId));
        
        // 2. For each contest to add:
        if (patchDto.getAddContestIds() != null) {
            for (Integer cId : patchDto.getAddContestIds()) {
                Contests contest = contestsRepository.findById(cId)
                    .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + cId));
                // Add participant to contest's set
                contest.getParticipants().add(cp);
                // Save the owning side (Contests is the owning side)
                contestsRepository.save(contest);
            }
        }
        
        // 3. For each contest to remove:
        if (patchDto.getRemoveContestIds() != null) {
            for (Integer cId : patchDto.getRemoveContestIds()) {
                Contests contest = contestsRepository.findById(cId)
                    .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + cId));
                contest.getParticipants().remove(cp);
                contestsRepository.save(contest);
            }
        }
        
        // Build a GET DTO with enriched data
        ContestParticipantsGetResrep getResrep = ContestParticipantsGetResrep.builder()
                .participant_id(cp.getParticipant_id())
                .sh_id(cp.getStudent() != null ? cp.getStudent().getSh_id() : null)
                .studentName(cp.getStudent() != null ? cp.getStudent().getName() : null)
                .personal_email(cp.getStudent() != null ? cp.getStudent().getPersonal_email() : null)
                .contestIds(cp.getContests().stream()
                        .map(Contests::getContestId)
                        .collect(Collectors.toSet()))
                .build();
        return getResrep;
    }

    @Override
    public ContestParticipantsGetResrep getById(String id) {
        ContestParticipants cp = repository.findByShId(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestParticipant not found with id: " + id));
        ContestParticipantsGetResrep resrep = ContestParticipantsGetResrep.builder()
                .participant_id(cp.getParticipant_id())
                .sh_id(cp.getStudent() != null ? cp.getStudent().getSh_id() : null)
                .studentName(cp.getStudent() != null ? cp.getStudent().getName() : null)
                .personal_email(cp.getStudent() != null ? cp.getStudent().getPersonal_email() : null)
                .build();
        if (cp.getContests() != null && !cp.getContests().isEmpty()) {
            Set<Integer> contestIds = cp.getContests().stream()
                    .map(Contests::getContestId)
                    .collect(Collectors.toSet());
            resrep.setContestIds(contestIds);
        } else {
            resrep.setContestIds(new HashSet<>());
        }
        return resrep;
    }

    @Override
    public ContestParticipantsResrep create(ContestParticipantsResrep resrep) {
        // Retrieve the student using the provided sh_id; throw exception if not found.
        Students student = studentsRepository.findByShId(resrep.getSh_id())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with sh_id: " + resrep.getSh_id()));
        
        // Retrieve the contest using the contestId; throw exception if not found.
        Contests contest = contestsRepository.findById(resrep.getContestId())
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + resrep.getContestId()));
        
        // Create a new ContestParticipants object and set the student.
        ContestParticipants cp = new ContestParticipants();
        cp.setStudent(student);
        cp.getContests().add(contest);
        
        // First persist the ContestParticipants entity so that it gets its generated primary key.
        cp = repository.save(cp);
        repository.flush();
        // Then update the contest's participants collection.
        contest.getParticipants().add(cp);
        // Save the contest to update the join table mapping.
        contestsRepository.save(contest);
        
        // Map the persisted ContestParticipants entity back to a response DTO.
        ContestParticipantsResrep savedResrep = modelMapper.map(cp, ContestParticipantsResrep.class);
        savedResrep.setContestId(contest.getContestId());
        savedResrep.setSh_id(student.getSh_id());
        return savedResrep;
    }


    @Override
    public void delete(Integer id) {
        ContestParticipants cp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestParticipant not found with id: " + id));
        repository.delete(cp);
    }

    @Override
    public List<ContestParticipantsResrep> getByContestId(Integer contestId) {
        List<ContestParticipants> list = repository.findByContestId(contestId);
        return list.stream()
                .map(cp -> {
                    ContestParticipantsResrep dto = modelMapper.map(cp, ContestParticipantsResrep.class);
                    // Since this method is for a given contest, set contestId in DTO
                    dto.setContestId(contestId);
                    // Also, set sh_id explicitly from the student relationship
                    if (cp.getStudent() != null) {
                        dto.setSh_id(cp.getStudent().getSh_id());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
