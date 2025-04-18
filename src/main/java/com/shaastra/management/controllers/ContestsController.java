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

import com.shaastra.management.handler.ContestsService;
import com.shaastra.management.resource_representation.ContestsResrep;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ContestsController {

    private final ContestsService contestsService;

    @GetMapping
    public ResponseEntity<List<ContestsResrep>> getAll() {
        return ResponseEntity.ok(contestsService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContestsResrep> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(contestsService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ContestsResrep> create(@Valid @RequestBody ContestsResrep resrep) {
        ContestsResrep created = contestsService.create(resrep);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContestsResrep> update(@PathVariable Integer id,
            @Valid @RequestBody ContestsResrep resrep) {
        return ResponseEntity.ok(contestsService.update(id, resrep));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ContestsResrep> partialUpdate(@PathVariable Integer id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(contestsService.partialUpdate(id, updates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        contestsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Extra method: Get upcoming contests (i.e. contests with a contest_date in the future)
    @GetMapping("/upcoming")
    public ResponseEntity<List<ContestsResrep>> getUpcomingContests() {
        return ResponseEntity.ok(contestsService.getUpcomingContests());
    }
}
