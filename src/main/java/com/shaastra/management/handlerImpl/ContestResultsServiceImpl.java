package com.shaastra.management.handlerImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.shaastra.management.entities.ContestParticipants;
import com.shaastra.management.entities.ContestResults;
import com.shaastra.management.entities.Contests;
import com.shaastra.management.exceptions.CustomBadRequestException;
import com.shaastra.management.exceptions.ResourceNotFoundException;
import com.shaastra.management.handler.ContestResultsService;
import com.shaastra.management.repositories.ContestParticipantsRepository;
import com.shaastra.management.repositories.ContestResultsRepository;
import com.shaastra.management.repositories.ContestsRepository;
import com.shaastra.management.resource_representation.ContestResultsResrep;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContestResultsServiceImpl implements ContestResultsService {

    private final ContestResultsRepository repository;
    private final ContestsRepository contestsRepository;
    private final ContestParticipantsRepository contestParticipantsRepository;
    
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(ContestResultsServiceImpl.class);

    @Override
    public List<ContestResultsResrep> getAll() {
        return repository.findAll().stream()
                .map(entity -> modelMapper.map(entity, ContestResultsResrep.class))
                .collect(Collectors.toList());
    }
    @Override
    public Integer getScoreForContestAndParticipant(Integer contestId, Integer participantId) {
        return repository.findScoreByContestIdAndParticipantId(contestId, participantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Score not found for contest id: " + contestId + " and participant id: " + participantId));
    }
    @Override
    public ContestResultsResrep getById(Integer id) {
        ContestResults cr = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestResults not found with id: " + id));
        return modelMapper.map(cr, ContestResultsResrep.class);
    }
    @Override
    public ContestResultsResrep create(ContestResultsResrep resrep) {
        try {
            // Fetch the related entities using the provided IDs
            Contests contest = contestsRepository.findById(resrep.getContest_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + resrep.getContest_id()));

            ContestParticipants participant = contestParticipantsRepository.findById(resrep.getParticipant_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + resrep.getParticipant_id()));

            // Create a new ContestResults entity
            ContestResults contestResults = new ContestResults();
            contestResults.setContest(contest);
            contestResults.setContestParticipant(participant);
            contestResults.setScore(resrep.getScore());
            contestResults.setRank_in_this_contest(resrep.getRank_in_this_contest());

            // If status is null, set default value
            if (resrep.getStatus() == null) {
                contestResults.setStatus("result-not-set");
            } else {
                contestResults.setStatus(resrep.getStatus());
            }

            // Save the entity
            contestResults = repository.save(contestResults);

            // Convert back to response DTO
            return modelMapper.map(contestResults, ContestResultsResrep.class);

        } catch (DataIntegrityViolationException dive) {
            throw new CustomBadRequestException("Duplicate or invalid data encountered while creating contest result.", dive);
        } catch (Exception ex) {
            throw new CustomBadRequestException("An unexpected error occurred while creating contest result.", ex);
        }
    }

    @Override
    public ContestResultsResrep update(Integer id, ContestResultsResrep resrep) {
        ContestResults cr = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestResults not found with id: " + id));
        // Update entity fields
        cr = repository.save(cr);
        return modelMapper.map(cr, ContestResultsResrep.class);
    }

    @Override
    public ContestResultsResrep partialUpdate(Integer id, Map<String, Object> updates) {
        ContestResults cr = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestResults not found with id: " + id));
        
        if (updates.containsKey("score")) {
            try {
                cr.setScore(((Number) updates.get("score")).intValue());
            } catch (Exception e) {
                throw new CustomBadRequestException("Invalid value for 'score'.", e);
            }
        }
        
        if (updates.containsKey("rank_in_this_contest")) {
            try {
                cr.setRank_in_this_contest(((Number) updates.get("rank_in_this_contest")).intValue());
            } catch (Exception e) {
                throw new CustomBadRequestException("Invalid value for 'rank_in_this_contest'.", e);
            }
        }
        
        if (updates.containsKey("status")) {
            Object statusObj = updates.get("status");
            if (statusObj instanceof String) {
                String status = (String) statusObj;
                if (!status.matches("result-invalid|result-confirmed|result-not-set")) {
                    throw new CustomBadRequestException("Invalid 'status' value. Allowed: result-invalid, result-confirmed, result-not-set.");
                }
                cr.setStatus(status);
            } else {
                throw new CustomBadRequestException("Invalid type for 'status'. Expected String.");
            }
        }
        
        // Save and map back to resrep
        cr = repository.save(cr);
        return modelMapper.map(cr, ContestResultsResrep.class);
    }

    @Override
    public void delete(Integer id) {
        ContestResults cr = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestResults not found with id: " + id));
        repository.delete(cr);
    }

    @Override
    public List<ContestResultsResrep> getRankingForContest(Integer contestId) {
        return repository.findRankingByContestId(contestId).stream()
                .map(entity -> {
                    ContestResultsResrep resrep = modelMapper.map(entity, ContestResultsResrep.class);
                    resrep.setContest_id(entity.getContest().getContestId());  // Explicitly set contestId
                    resrep.setParticipant_id(entity.getContestParticipant().getParticipant_id());  // Explicitly set participantId
                    return resrep;
                })
                .collect(Collectors.toList());
    }

}
