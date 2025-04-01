package com.shaastra.management.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shaastra.management.handler.ContestParticipantsService;
import com.shaastra.management.resource_representation.ContestParticipantsGetResrep;
import com.shaastra.management.resource_representation.ContestParticipantsPatchResrep;
import com.shaastra.management.resource_representation.ContestParticipantsResrep;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contest-participants")
@RequiredArgsConstructor
public class ContestParticipantsController {

    private final ContestParticipantsService contestParticipantsService;

    @GetMapping
    public ResponseEntity<List<ContestParticipantsGetResrep>> getAll() {
        return ResponseEntity.ok(contestParticipantsService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContestParticipantsGetResrep> getById(@PathVariable String id) {
        return ResponseEntity.ok(contestParticipantsService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ContestParticipantsResrep> create(@Valid @RequestBody ContestParticipantsResrep resrep) {
        ContestParticipantsResrep created = contestParticipantsService.create(resrep);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ContestParticipantsGetResrep> patchUpdate(@PathVariable Integer id,
            @RequestBody ContestParticipantsPatchResrep patchResrep) {
        ContestParticipantsGetResrep updated = contestParticipantsService.patchUpdate(id, patchResrep);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        contestParticipantsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Extra method: Get all participants for a given contest.
    @GetMapping("/by-contest/{contestId}")  
    
    /*FIX THIS CONTROLLER , WANT ALL VALUES TO RETURN*/
    
    public ResponseEntity<List<ContestParticipantsResrep>> getByContestId(@PathVariable Integer contestId) {
        return ResponseEntity.ok(contestParticipantsService.getByContestId(contestId));
    }
}
