package com.shaastra.management.handlerImpl;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shaastra.management.entities.ContestProblem;
import com.shaastra.management.entities.Contests;
import com.shaastra.management.exceptions.CustomBadRequestException;
import com.shaastra.management.exceptions.ResourceNotFoundException;
import com.shaastra.management.handler.ContestsService;
import com.shaastra.management.repositories.ContestProblemRepository;
import com.shaastra.management.repositories.ContestsRepository;
import com.shaastra.management.resource_representation.ContestsResrep;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContestsServiceImpl implements ContestsService {

    private final ContestsRepository repository;
    private final ModelMapper modelMapper;
    private final ContestProblemRepository contestProblemRepository;
    private static final Logger logger = LoggerFactory.getLogger(ContestsServiceImpl.class);

    @Override
    public List<ContestsResrep> getAll() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ContestsResrep getById(Integer id) {
        Contests contest = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + id));
        return mapToResponse(contest);
    }

    @Override
    public ContestsResrep create(ContestsResrep resrep) {
        try {
            // Validate input: ensure contestProblemIds is not null
            if (resrep.getContestProblemIds() == null) {
                throw new CustomBadRequestException("ContestProblemIds cannot be null.");
            }
            // Fetch and validate ContestProblem entities using provided IDs
            Set<ContestProblem> contestProblems = resrep.getContestProblemIds().stream()
                    .map(problemId -> contestProblemRepository.findById(problemId)
                            .orElseThrow(() -> new ResourceNotFoundException("ContestProblem not found with id: " + problemId)))
                    .collect(Collectors.toSet());
            // Create a new Contest entity and set fields from the DTO
            Contests contest = new Contests();
            contest.setContest_name(resrep.getContest_name());
            contest.setContest_description(resrep.getContest_description());
            contest.setContest_link(resrep.getContest_link());
            contest.setContest_date(resrep.getContest_date());
            contest.setStatus("scheduled");  // Default status for new contest
            contest.setTotal_participants(0);  // Initially zero participants
            contest.setContestProblems(contestProblems);
            // Save the contest entity
            contest = repository.save(contest);
            logger.info("Successfully created contest with id: {}", contest.getContestId());
            return mapToResponse(contest);
        } catch (DataIntegrityViolationException dive) {
            logger.error("Data integrity violation while creating contest: {}", dive.getMessage(), dive);
            throw new CustomBadRequestException("Duplicate or invalid data encountered while creating contest.", dive);
        } catch (CustomBadRequestException cbre) {
            logger.error("Bad request error while creating contest: {}", cbre.getMessage(), cbre);
            throw cbre;
        } catch (Exception ex) {
            logger.error("Unexpected error while creating contest: {}", ex.getMessage(), ex);
            throw new CustomBadRequestException("An unexpected error occurred while creating contest.", ex);
        }
    }

    @Override
    public ContestsResrep update(Integer id, ContestsResrep resrep) {
        Contests contest = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + id));
        // Update mutable fields
        contest.setContest_name(resrep.getContest_name());
        contest.setContest_description(resrep.getContest_description());
        contest.setContest_link(resrep.getContest_link());
        contest.setContest_date(resrep.getContest_date());
        contest.setStatus(resrep.getStatus());
        contest.setTotal_participants(resrep.getTotal_participants());
        // Optionally update contest problems if provided
        if (resrep.getContestProblemIds() != null) {
            Set<ContestProblem> contestProblems = resrep.getContestProblemIds().stream()
                    .map(problemId -> contestProblemRepository.findById(problemId)
                            .orElseThrow(() -> new ResourceNotFoundException("ContestProblem not found with id: " + problemId)))
                    .collect(Collectors.toSet());
            contest.setContestProblems(contestProblems);
        }
        contest = repository.save(contest);
        return mapToResponse(contest);
    }

    @Override
    public ContestsResrep partialUpdate(Integer id, Map<String, Object> updates) {
        Contests contest = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + id));
        if (updates.containsKey("contest_description")) {
            contest.setContest_description((String) updates.get("contest_description"));
        }
        if (updates.containsKey("contest_name")) {
        	contest.setContest_name((String) updates.get("contest_name"));
        }
        if (updates.containsKey("total_participants")) {
            Object tpObj = updates.get("total_participants");
            if (tpObj instanceof Number) {
                contest.setTotal_participants(((Number) tpObj).intValue());
            } else {
                throw new CustomBadRequestException("Invalid type for 'total_participants'. Expected numeric value.");
            }
        }
        if (updates.containsKey("contest_link")) {
            contest.setContest_link((String) updates.get("contest_link"));
        }
        if (updates.containsKey("contest_date")) {
            // Assuming the value is an OffsetDateTime instance or already converted appropriately
            contest.setContest_date((OffsetDateTime) updates.get("contest_date"));
        }
        if (updates.containsKey("status")) {
            contest.setStatus((String) updates.get("status"));
        }
        if (updates.containsKey("contestProblemIds")) {
            @SuppressWarnings("unchecked")
            List<Integer> problemIds = (List<Integer>) updates.get("contestProblemIds");
            Set<ContestProblem> updatedProblems = problemIds.stream()
                    .map(problemId -> contestProblemRepository.findById(problemId)
                            .orElseThrow(() -> new ResourceNotFoundException("ContestProblem not found with id: " + problemId)))
                    .collect(Collectors.toSet());
            contest.setContestProblems(updatedProblems);
        }
        contest = repository.save(contest);
        return mapToResponse(contest);
    }

    @Override
    public void delete(Integer id) {
        Contests contest = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + id));
        repository.delete(contest);
    }

    @Override
    public Page<ContestsResrep> getUpcomingContests(OffsetDateTime dateTime, Pageable pageable) {
        Page<Contests> contestsPage = repository.findByContestDateAfter(dateTime, pageable);
        return contestsPage.map(this::mapToResponse);
    }


    private ContestsResrep mapToResponse(Contests contest) {
        // Base mapping via ModelMapper
        ContestsResrep dto = modelMapper.map(contest, ContestsResrep.class);
        // Explicitly set contestProblemIds from the entity's relationship
        if (contest.getContestProblems() != null && !contest.getContestProblems().isEmpty()) {
            dto.setContestProblemIds(
                contest.getContestProblems().stream()
                       .map(ContestProblem::getContest_problem_id)
                       .collect(Collectors.toSet())
            );
        } else {
            dto.setContestProblemIds(new HashSet<>());
        }
        return dto;
    }
}
