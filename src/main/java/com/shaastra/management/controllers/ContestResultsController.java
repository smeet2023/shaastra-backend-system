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

import com.shaastra.management.handler.ContestResultsService;
import com.shaastra.management.resource_representation.ContestResultPostResRep;
import com.shaastra.management.resource_representation.ContestResultsResrep;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contest-results")
@RequiredArgsConstructor
public class ContestResultsController {

	private final ContestResultsService contestResultsService;
	@GetMapping("/contest/{contestId}/participant/{participantId}/score")
    public ResponseEntity<Integer> getScore(@PathVariable Integer contestId,
                                              @PathVariable String participantId) {
        Integer score = contestResultsService.getScoreForContestAndParticipant(contestId, participantId);
        return ResponseEntity.ok(score);
    }
	@GetMapping
	public ResponseEntity<List<ContestResultsResrep>> getAll() {
		return ResponseEntity.ok(contestResultsService.getAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ContestResultsResrep> getById(@PathVariable Integer id) {
		return ResponseEntity.ok(contestResultsService.getById(id));
	}

	@PostMapping
	public ResponseEntity<ContestResultsResrep> create(@Valid @RequestBody ContestResultPostResRep resrep) {
		ContestResultsResrep created = contestResultsService.create(resrep);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ContestResultsResrep> update(@PathVariable Integer id,
			@Valid @RequestBody ContestResultsResrep resrep) {
		return ResponseEntity.ok(contestResultsService.update(id, resrep));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ContestResultsResrep> partialUpdate(@PathVariable Integer id,
			@RequestBody Map<String, Object> updates) {
		return ResponseEntity.ok(contestResultsService.partialUpdate(id, updates));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		contestResultsService.delete(id);
		return ResponseEntity.noContent().build();
	}

	// Extra method: Get contest ranking (sorted results) for a given contest.
	@GetMapping("/contest/{contestId}/ranking")
	public ResponseEntity<List<ContestResultsResrep>> getRankingForContest(@PathVariable Integer contestId) {
		return ResponseEntity.ok(contestResultsService.getRankingForContest(contestId));
	}
}
