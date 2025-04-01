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
public class ContestParticipantsServiceImpl implements ContestParticipantsService 
{

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
                // Build the DTO manually
                ContestParticipantsGetResrep resrep = ContestParticipantsGetResrep.builder()
                        .sh_id(cp.getStudent() != null ? cp.getStudent().getSh_id() : null)
                        .build();
                // Map the associated contest IDs
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

    public ContestParticipantsGetResrep patchUpdate(Integer participantId, ContestParticipantsPatchResrep patchDto) {
        // 1. Fetch the participant
        ContestParticipants cp = repository.findById(participantId)
            .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        // 2. For each contest to add:
        if (patchDto.getAddContestIds() != null) {
            for (Integer cId : patchDto.getAddContestIds()) {
                Contests contest = contestsRepository.findById(cId)
                    .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + cId));
                // Add the participant to the contest's set
                contest.getParticipants().add(cp);
                // Save the owning side
                contestsRepository.save(contest);
            }
        }

        // 3. For each contest to remove:
        if (patchDto.getRemoveContestIds() != null) {
            for (Integer cId : patchDto.getRemoveContestIds()) {
                Contests contest = contestsRepository.findById(cId)
                    .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + cId));
                // Remove the participant from the contest's set
                contest.getParticipants().remove(cp);
                // Save the owning side
                contestsRepository.save(contest);
            }
        }
        ContestParticipantsGetResrep getResrep = ContestParticipantsGetResrep.builder()
                .sh_id(cp.getStudent() != null ? cp.getStudent().getSh_id() : null)
                .contestIds(cp.getContests().stream()
                        .map(Contests::getContestId)
                        .collect(Collectors.toSet()))
                .build();
        // 4. Build a GET response
        // (assuming you have a getResrepFromParticipant() method or do it manually)
        return getResrep;
    }




    @Override
    public ContestParticipantsGetResrep getById(String id) {
        ContestParticipants cp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestParticipant not found with id: " + id));
        
        // Map the basic fields using ModelMapper if needed (here, we set sh_id explicitly)
        ContestParticipantsGetResrep resrep = ContestParticipantsGetResrep.builder()
                .sh_id(cp.getStudent() != null ? cp.getStudent().getSh_id() : null)
                .build();
        
        // Map the associated contests (many-to-many) to a set of contestIds
        if (cp.getContests() != null && !cp.getContests().isEmpty()) {
            Set<Integer> contestIds = cp.getContests().stream()
                    .map(Contests::getContestId)
                    .collect(Collectors.toSet());
            resrep.setContestIds(contestIds);
        } else {
            resrep.setContestIds(new HashSet<>()); // or null, depending on your design
        }
        
        return resrep;
    }



    @Override
    public ContestParticipantsResrep create(ContestParticipantsResrep resrep) {
        // Ensure the student with provided sh_id exists
        Students student = studentsRepository.findByShId(resrep.getSh_id())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with sh_id: " + resrep.getSh_id()));
        
        // Ensure the contest with provided contest_id exists
        Contests contest = contestsRepository.findById(resrep.getContestId())
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + resrep.getContestId()));
        
        // Create a new ContestParticipants record
        ContestParticipants cp = new ContestParticipants();
        cp.setStudent(student);
        
        // Since ContestParticipants is mapped as ManyToMany (non-owning side) we update the owning side:
        contest.getParticipants().add(cp);
        
        // Save the participant first (or alternatively, save contest to update the join table)
        cp = repository.save(cp);
        // Save contest to ensure the join table is updated
        contestsRepository.save(contest);
        
        // Map the saved entity to our resrep. Ensure participantId is populated and contestId is set back.
        ContestParticipantsResrep savedResrep = modelMapper.map(cp, ContestParticipantsResrep.class);
        savedResrep.setContestId(contest.getContestId());
        savedResrep.setSh_id(cp.getStudent().getSh_id());
        
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
                    // Since this method is for a given contest, assign the contestId to every DTO.
                    dto.setContestId(contestId);
                    // Ensure sh_id is correctly set from the associated student.
                    if (cp.getStudent() != null) {
                        dto.setSh_id(cp.getStudent().getSh_id());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
