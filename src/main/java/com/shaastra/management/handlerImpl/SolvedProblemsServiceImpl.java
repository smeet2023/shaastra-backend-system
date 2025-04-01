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
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SolvedProblemsResrep getById(Integer id) {
        SolvedProblems sp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SolvedProblems not found with id: " + id));
        return mapToResponse(sp);
    }

    @Override
    public List<SolvedProblemsResrep> create(List<SolvedProblemsResrep> requests) {
        List<SolvedProblems> solvedProblemsList = new ArrayList<>();
        for (SolvedProblemsResrep req : requests) {
            // Fetch related entities using the enriched identifiers
            ContestParticipants cp = contestParticipantsRepository.findById(req.getContest_participant_id())
                    .orElseThrow(() -> new ResourceNotFoundException("ContestParticipant not found with id: " + req.getContest_participant_id()));
            Contests contest = contestsRepository.findById(req.getContest_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + req.getContest_id()));
            ContestProblem cpProblem = contestProblemRepository.findById(req.getContest_problem_id())
                    .orElseThrow(() -> new ResourceNotFoundException("ContestProblem not found with id: " + req.getContest_problem_id()));
            
            SolvedProblems sp = new SolvedProblems();
            sp.setContestParticipant(cp);
            sp.setContest(contest);
            sp.setContestProblem(cpProblem);
            sp.setScore(req.getScore());
            solvedProblemsList.add(sp);
        }
        repository.saveAll(solvedProblemsList);
        return solvedProblemsList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SolvedProblemsResrep update(Integer id, SolvedProblemsResrep resrep) {
        SolvedProblems sp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SolvedProblems not found with id: " + id));
        // For full update, you may update mutable fields; here we update only score
        sp.setScore(resrep.getScore());
        sp = repository.save(sp);
        return mapToResponse(sp);
    }

    @Override
    public SolvedProblemsResrep partialUpdate(Integer id, Map<String, Object> updates) {
        SolvedProblems sp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SolvedProblems not found with id: " + id));
        try {
            if (updates.containsKey("score")) {
                Object scoreObj = updates.get("score");
                if (scoreObj instanceof Number) {
                    sp.setScore(((Number) scoreObj).intValue());
                } else {
                    throw new CustomBadRequestException("Invalid type for 'score'. Expected a numeric value.");
                }
            }
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
        return mapToResponse(sp);
    }

    @Override
    public void delete(Integer id) {
        SolvedProblems sp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SolvedProblems not found with id: " + id));
        repository.delete(sp);
    }

    @Override
    public List<SolvedProblemsResrep> getByParticipantId(String participantId) {
        return repository.findByParticipantId(participantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SolvedProblemsResrep> getByContestAndParticipant(Integer contestId, String participantId) {
        return repository.findByContestIdAndParticipantId(contestId, participantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Integer getTotalMarks(Integer contestId, String participantId) {
        return repository.findTotalScoreByContestIdAndParticipantId(contestId, participantId);
    }

    private SolvedProblemsResrep mapToResponse(SolvedProblems sp) {
        // Base mapping via ModelMapper
        SolvedProblemsResrep dto = modelMapper.map(sp, SolvedProblemsResrep.class);
        // Set the unique solved problem record ID
        dto.setSolved_problem_id(sp.getSp_id());
        // Set contest participant details
        if (sp.getContestParticipant() != null) {
            dto.setContest_participant_id(sp.getContestParticipant().getParticipant_id());
            dto.setParticipantName(
                sp.getContestParticipant().getStudent() != null ? 
                sp.getContestParticipant().getStudent().getName() : null
            );
        }
        // Set contest details
        if (sp.getContest() != null) {
            dto.setContest_id(sp.getContest().getContestId());
            dto.setContestName(sp.getContest().getContest_description());
        }
        // Set contest problem details
        if (sp.getContestProblem() != null) {
            dto.setContest_problem_id(sp.getContestProblem().getContest_problem_id());
            dto.setProblem_title(sp.getContestProblem().getProblem_title());
        }
        dto.setScore(sp.getScore());
        return dto;
    }
}