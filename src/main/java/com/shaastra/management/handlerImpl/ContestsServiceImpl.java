package com.shaastra.management.handlerImpl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
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
                .map(entity -> {
                    ContestsResrep dto = modelMapper.map(entity, ContestsResrep.class);
                    if (entity.getContestProblems() != null) {
                        dto.setContestProblemIds(
                            entity.getContestProblems().stream()
                                  .map(ContestProblem::getContest_problem_id)
                                  .collect(Collectors.toSet())
                        );
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ContestsResrep getById(Integer id) {
        Contests contest = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + id));
        ContestsResrep dto = modelMapper.map(contest, ContestsResrep.class);
        if (contest.getContestProblems() != null) {
            dto.setContestProblemIds(
                contest.getContestProblems().stream()
                       .map(ContestProblem::getContest_problem_id)
                       .collect(Collectors.toSet())
            );
        }
        return dto;
    }


    @Override
    public ContestsResrep create(ContestsResrep resrep) {
        try {
            // Validate input: ensure contestProblemIds is not null (if needed)
            if (resrep.getContestProblemIds() == null) {
                throw new CustomBadRequestException("ContestProblemIds cannot be null.");
            }
            
            // Fetch and validate contest problems using the IDs provided
            Set<ContestProblem> contestProblems = resrep.getContestProblemIds().stream()
                .map(id -> contestProblemRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("ContestProblem not found with id: " + id)))
                .collect(Collectors.toSet());
            
            // Create new Contest entity
            Contests contest = new Contests();
            contest.setContest_description(resrep.getContest_description());
            contest.setContest_link(resrep.getContest_link());
            contest.setContest_date(resrep.getContest_date());
            
            // Set default or initial values
            contest.setStatus("scheduled");  
            contest.setTotal_participants(0);
            
            // Set the contest problems from the validated set
            contest.setContestProblems(contestProblems);
            
            // Save the contest
            contest = repository.save(contest);
            
            // Map to resrep to include generated id, etc.
            ContestsResrep createdResrep = modelMapper.map(contest, ContestsResrep.class);
            // Ensure contestProblemIds are set back from the entity:
            if (contest.getContestProblems() != null) {
                createdResrep.setContestProblemIds(contest.getContestProblems().stream()
                    .map(ContestProblem::getContest_problem_id)
                    .collect(Collectors.toSet()));
            }
            
            logger.info("Successfully created contest with id: {}", contest.getContestId());
            return createdResrep;
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
        // Update fields as necessary
        contest = repository.save(contest);
        return modelMapper.map(contest, ContestsResrep.class);
    }

    @Override
    public ContestsResrep partialUpdate(Integer id, Map<String, Object> updates) {
        Contests contest = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + id));
        
        // Example of handling fields manually:
        if (updates.containsKey("contest_description")) {
            contest.setContest_description((String) updates.get("contest_description"));
        }
        if (updates.containsKey("total_participants")) {
        	contest.setTotal_participants((Integer) updates.get("total_participants"));
        }
        if (updates.containsKey("contest_link")) {
            contest.setContest_link((String) updates.get("contest_link"));
        }
        if (updates.containsKey("contestProblemIds")) {
            // Retrieve the list of IDs from the update payload
            @SuppressWarnings("unchecked")
            List<Integer> problemIds = (List<Integer>) updates.get("contestProblemIds");
            // Fetch each ContestProblem entity by ID and collect them
            Set<ContestProblem> updatedProblems = problemIds.stream()
                .map(problemId -> contestProblemRepository.findById(problemId)
                    .orElseThrow(() -> new ResourceNotFoundException("ContestProblem not found with id: " + problemId)))
                .collect(Collectors.toSet());
            // Update the entity's contestProblems set
            contest.setContestProblems(updatedProblems);
        }
        if (updates.containsKey("contest_date")) {
            // For date conversion, you might need to convert from String to OffsetDateTime
            // Assuming the value is already an OffsetDateTime or converted appropriately
            contest.setContest_date((OffsetDateTime) updates.get("contest_date"));
        }
        if (updates.containsKey("status")) {
            contest.setStatus((String) updates.get("status"));
        }
        // Continue for other fields...

        contest = repository.save(contest);
        ContestsResrep updatedResrep = modelMapper.map(contest, ContestsResrep.class);
        if (contest.getContestProblems() != null) {
            updatedResrep.setContestProblemIds(contest.getContestProblems().stream()
                .map(ContestProblem::getContest_problem_id)
                .collect(Collectors.toSet()));
        }
        return modelMapper.map(contest, ContestsResrep.class);
    }


    @Override
    public void delete(Integer id) {
        Contests contest = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + id));
        repository.delete(contest);
    }

    @Override
    public List<ContestsResrep> getUpcomingContests() {
        OffsetDateTime now = OffsetDateTime.now();
        return repository.findByContestDateAfter(now).stream()
                .map(entity -> modelMapper.map(entity, ContestsResrep.class))
                .collect(Collectors.toList());
    }
}
