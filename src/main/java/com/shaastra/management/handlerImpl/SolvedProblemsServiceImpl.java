package com.shaastra.management.handlerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shaastra.management.entities.ContestParticipants;
import com.shaastra.management.entities.ContestProblem;
import com.shaastra.management.entities.Contests;
import com.shaastra.management.entities.SolvedProblems;
import com.shaastra.management.exceptions.CustomBadRequestException;
import com.shaastra.management.exceptions.ResourceNotFoundException;
import com.shaastra.management.handler.SolvedProblemsService;
import com.shaastra.management.repositories.ContestParticipantsRepository;
import com.shaastra.management.repositories.ContestProblemRepository;
import com.shaastra.management.repositories.ContestsRepository;
import com.shaastra.management.repositories.SolvedProblemsRepository;
import com.shaastra.management.resource_representation.SolvedProblemsResrep;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolvedProblemsServiceImpl implements SolvedProblemsService {
	@Autowired
    private final SolvedProblemsRepository repository;
    private final ModelMapper modelMapper;
    @Autowired
    private ContestParticipantsRepository contestParticipantsRepository;
    @Autowired
    private ContestsRepository contestsRepository;
    @Autowired
    private ContestProblemRepository contestProblemRepository;
    
    @Override
    public List<SolvedProblemsResrep> getAll() {
        return repository.findAll().stream()
                .map(entity -> modelMapper.map(entity, SolvedProblemsResrep.class))
                .collect(Collectors.toList());
    }

    @Override
    public SolvedProblemsResrep getById(Integer id) {
        SolvedProblems sp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SolvedProblems not found with id: " + id));
        return modelMapper.map(sp, SolvedProblemsResrep.class);
    }
    @Override
    public List<SolvedProblemsResrep> create(List<SolvedProblemsResrep> requests) {
        List<SolvedProblems> solvedProblemsList = new ArrayList<>();
        for (SolvedProblemsResrep req : requests) {
        	System.out.println("part id " + req.getContest_participant_id());
        	System.out.println("contest id "+ req.getContest_id());
        	System.out.println("probelm id " + req.getContest_problem_id());
        	System.out.println("score " + req.getScore());
            SolvedProblems sp = new SolvedProblems();
            sp.setContestParticipant(contestParticipantsRepository.findById(req.getContest_participant_id())
                .orElseThrow(() -> new ResourceNotFoundException("ContestParticipant not found with id: " + req.getContest_participant_id())));

            sp.setContest(contestsRepository.findById(req.getContest_id())
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + req.getContest_id())));

            sp.setContestProblem(contestProblemRepository.findById(req.getContest_problem_id())
                .orElseThrow(() -> new ResourceNotFoundException("ContestProblem not found with id: " + req.getContest_problem_id())));

            sp.setScore(req.getScore());
            solvedProblemsList.add(sp);
        }

        repository.saveAll(solvedProblemsList);
        return solvedProblemsList.stream()
                .map(entity -> {
                    SolvedProblemsResrep dto = modelMapper.map(entity, SolvedProblemsResrep.class);
                    if (entity.getContestParticipant() != null) {
                        dto.setContest_participant_id(entity.getContestParticipant().getParticipant_id());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public SolvedProblemsResrep update(Integer id, SolvedProblemsResrep resrep) {
        SolvedProblems sp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SolvedProblems not found with id: " + id));
        // Update fields as necessary
        sp = repository.save(sp);
        return modelMapper.map(sp, SolvedProblemsResrep.class);
    }

    @Override
    public SolvedProblemsResrep partialUpdate(Integer id, Map<String, Object> updates) {
        SolvedProblems sp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SolvedProblems not found with id: " + id));

        try {
            // Update score
            if (updates.containsKey("score")) {
                Object scoreObj = updates.get("score");
                if (scoreObj instanceof Number) {
                    sp.setScore(((Number) scoreObj).intValue());
                } else {
                    throw new CustomBadRequestException("Invalid type for 'score'. Expected a numeric value.");
                }
            }

            // Update contest_participant
            if (updates.containsKey("contest_participant_id")) {
                Object cpIdObj = updates.get("contest_participant_id");
                if (cpIdObj instanceof Number) {
                    Integer cpId = ((Number) cpIdObj).intValue();
                    ContestParticipants cp = contestParticipantsRepository.findById(cpId)
                            .orElseThrow(() -> new ResourceNotFoundException("ContestParticipant not found with id: " + cpId));
                    sp.setContestParticipant(cp);
                } else {
                    throw new CustomBadRequestException("Invalid type for 'contest_participant_id'. Expected a numeric value.");
                }
            }

            // Update contest
            if (updates.containsKey("contest_id")) {
                Object contestIdObj = updates.get("contest_id");
                if (contestIdObj instanceof Number) {
                    Integer contestId = ((Number) contestIdObj).intValue();
                    Contests contest = contestsRepository.findById(contestId)
                            .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + contestId));
                    sp.setContest(contest);
                } else {
                    throw new CustomBadRequestException("Invalid type for 'contest_id'. Expected a numeric value.");
                }
            }

            // Update contest_problem
            if (updates.containsKey("contest_problem_id")) {
                Object cpProblemIdObj = updates.get("contest_problem_id");
                if (cpProblemIdObj instanceof Number) {
                    Integer contestProblemId = ((Number) cpProblemIdObj).intValue();
                    ContestProblem cpProblem = contestProblemRepository.findById(contestProblemId)
                            .orElseThrow(() -> new ResourceNotFoundException("ContestProblem not found with id: " + contestProblemId));
                    sp.setContestProblem(cpProblem);
                } else {
                    throw new CustomBadRequestException("Invalid type for 'contest_problem_id'. Expected a numeric value.");
                }
            }

        } catch (ClassCastException | IllegalArgumentException ex) {
            throw new CustomBadRequestException("Error processing partial update: " + ex.getMessage(), ex);
        }

        sp = repository.save(sp);

        // **Manually setting values to ensure correct mapping**
        SolvedProblemsResrep resrep = new SolvedProblemsResrep();
        resrep.setScore(sp.getScore());
        resrep.setContest_problem_id((sp.getContestProblem().getContest_problem_id()));
        resrep.setContest_participant_id((sp.getContestParticipant() != null ? sp.getContestParticipant().getParticipant_id() : null));
        resrep.setContest_id((sp.getContest() != null ? sp.getContest().getContestId() : null));

        return resrep;
    }


    @Override
    public void delete(Integer id) {
        SolvedProblems sp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SolvedProblems not found with id: " + id));
        repository.delete(sp);
    }

    @Override
    public List<SolvedProblemsResrep> getByParticipantId(Integer participantId) {
        return repository.findByContestParticipant_Participant_id(participantId).stream()
                .map(entity -> {
                    SolvedProblemsResrep dto = modelMapper.map(entity, SolvedProblemsResrep.class);
                    if (entity.getContestParticipant() != null) {
                        dto.setContest_participant_id((entity.getContestParticipant().getParticipant_id()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
    @Override
    public List<SolvedProblemsResrep> getByContestAndParticipant(Integer contestId, Integer participantId) {
        return repository.findByContestIdAndParticipantId(contestId, participantId).stream()
                .map(entity -> {
                    SolvedProblemsResrep dto = modelMapper.map(entity, SolvedProblemsResrep.class);
                    // Manually assign contestId from the Contest relationship
                    if (entity.getContest() != null) {
                        dto.setContest_id(entity.getContest().getContestId());
                    }
                    // Manually assign participantId from the ContestParticipants relationship
                    if (entity.getContestParticipant() != null) {
                        dto.setContest_participant_id(entity.getContestParticipant().getParticipant_id());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Integer getTotalMarks(Integer contestId, Integer participantId) {
        return repository.findTotalScoreByContestIdAndParticipantId(contestId, participantId);
    }
}
