package com.shaastra.management.handlerImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.shaastra.management.resource_representation.ContestResultPostResRep;
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
                .map(entity -> mapToResponse(entity))
                .collect(Collectors.toList());
    }
    @Override
    public Integer getScoreForContestAndParticipant(Integer contestId, String participantId) {
        return repository.findScoreByContestIdAndParticipantId(contestId, participantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Score not found for contest id: " + contestId + " and participant id: " + participantId));
    }
    @Override
    public ContestResultsResrep getById(Integer id) {
        ContestResults cr = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestResults not found with id: " + id));
        return mapToResponse(cr);
    }
    @Override
    public ContestResultsResrep create(ContestResultPostResRep resrep) {
        Contests contest = contestsRepository.findById(resrep.getContest_id())
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found with id: " + resrep.getContest_id()));
        ContestParticipants participant = contestParticipantsRepository.findById(resrep.getParticipant_id())
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + resrep.getParticipant_id()));
        ContestResults contestResults = new ContestResults();
        contestResults.setContest(contest);
        contestResults.setContestParticipant(participant);
        contestResults.setScore(resrep.getScore());
        contestResults.setRank_in_this_contest(resrep.getRank_in_this_contest());
        contestResults.setStatus(Optional.ofNullable(resrep.getStatus()).orElse("result-not-set"));
        contestResults = repository.save(contestResults);
        return mapToResponse(contestResults);
    }
    @Override
    public ContestResultsResrep update(Integer id, ContestResultsResrep resrep) {
        ContestResults cr = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestResults not found with id: " + id));
        cr.setScore(resrep.getScore());
        cr.setRank_in_this_contest(resrep.getRank_in_this_contest());
        cr.setStatus(resrep.getStatus());
        cr = repository.save(cr);
        return mapToResponse(cr);
    }
    @Override
    public ContestResultsResrep partialUpdate(Integer id, Map<String, Object> updates) {
        ContestResults cr = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestResults not found with id: " + id));

        // Apply updates safely without lambda issues
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            switch (key) {
                case "score":
                    cr.setScore(((Number) value).intValue());
                    break;
                case "rank_in_this_contest":
                    cr.setRank_in_this_contest(((Number) value).intValue());
                    break;
                case "status":
                    if (!(value instanceof String)) {
                        throw new CustomBadRequestException("Invalid type for 'status'. Expected String.");
                    }
                    String status = (String) value;
                    if (!status.matches("result-invalid|result-confirmed|result-not-set")) {
                        throw new CustomBadRequestException("Invalid 'status' value. Allowed: result-invalid, result-confirmed, result-not-set.");
                    }
                    cr.setStatus(status);
                    break;
                default:
                    throw new CustomBadRequestException("Unknown field: " + key);
            }
        }

        // Save the updated entity
        cr = repository.save(cr);
        return mapToResponse(cr);
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
                .map(entity -> mapToResponse(entity))
                .collect(Collectors.toList());
    }
//    private ContestResultsResrep mapToResponse(ContestResults entity) {
//        ContestResultsResrep resrep = modelMapper.map(entity, ContestResultsResrep.class);
//        resrep.setContest_id(entity.getContest().getContestId());
//        resrep.setParticipant_id(entity.getContestParticipant().getParticipant_id());
//        return resrep;
//    }
    private ContestResultsResrep mapToResponse(ContestResults entity) {
        ContestResultsResrep dto = modelMapper.map(entity, ContestResultsResrep.class);
        dto.setContest_id(entity.getContest().getContestId());
        dto.setContest_name(entity.getContest().getContest_name());
        dto.setParticipant_id(entity.getContestParticipant().getParticipant_id());
        // Explicitly set participant name from the associated Student entity
        if (entity.getContestParticipant() != null && entity.getContestParticipant().getStudent() != null) {
            dto.setParticipant_name(entity.getContestParticipant().getStudent().getName());
        }
        dto.setScore(entity.getScore());
        dto.setRank_in_this_contest(entity.getRank_in_this_contest());
        dto.setStatus(entity.getStatus());
        return dto;
    }

}