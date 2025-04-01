package com.shaastra.management.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shaastra.management.handler.SolvedProblemsService;
import com.shaastra.management.resource_representation.SolvedProblemsResrep;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/solved-problems")
@RequiredArgsConstructor
public class SolvedProblemsController {

    private final SolvedProblemsService solvedProblemsService;
    @GetMapping("/contest/{contestId}/participant/{participantId}/solved-problems")
    public ResponseEntity<List<SolvedProblemsResrep>> getSolvedProblemsByContestAndParticipant(
            @PathVariable Integer contestId, 
            @PathVariable String participantId) {
        return ResponseEntity.ok(solvedProblemsService.getByContestAndParticipant(contestId, participantId));
    }

    @GetMapping
    public ResponseEntity<List<SolvedProblemsResrep>> getAll() {
        return ResponseEntity.ok(solvedProblemsService.getAll());
    }
    @GetMapping("/contest/{contestId}/participant/{participantId}/total-marks")
    public ResponseEntity<Integer> getTotalMarks(@PathVariable Integer contestId,
                                                 @PathVariable String participantId) {
        Integer totalMarks = solvedProblemsService.getTotalMarks(contestId, participantId);
        return ResponseEntity.ok(totalMarks);
    }
    @GetMapping("/{id}")
    public ResponseEntity<SolvedProblemsResrep> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(solvedProblemsService.getById(id));
    }

    @PostMapping
    public ResponseEntity<List<SolvedProblemsResrep>> createBatch(@RequestBody List<SolvedProblemsResrep> requests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solvedProblemsService.create(requests));
    }


    @PutMapping("/{id}")
    public ResponseEntity<SolvedProblemsResrep> update(@PathVariable Integer id,
            @Valid @RequestBody SolvedProblemsResrep resrep) {
        return ResponseEntity.ok(solvedProblemsService.update(id, resrep));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SolvedProblemsResrep> partialUpdate(@PathVariable Integer id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(solvedProblemsService.partialUpdate(id, updates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        solvedProblemsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Extra method: Get solved problems for a specific contest participant.
    @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<SolvedProblemsResrep>> getByParticipant(@PathVariable String participantId) {
        return ResponseEntity.ok(solvedProblemsService.getByParticipantId(participantId));
    }
}
