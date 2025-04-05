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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shaastra.management.handler.ContestProblemService;
import com.shaastra.management.resource_representation.ContestProblemResrep;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contest-problems")
@RequiredArgsConstructor
public class ContestProblemController {

    private final ContestProblemService contestProblemService;

    @GetMapping
    public ResponseEntity<List<ContestProblemResrep>> getAll() {
        return ResponseEntity.ok(contestProblemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContestProblemResrep> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(contestProblemService.getById(id));
    }

    @PostMapping("/add-new-problems")
    public ResponseEntity<ContestProblemResrep> create(@Valid @RequestBody ContestProblemResrep resrep) {
        ContestProblemResrep created = contestProblemService.create(resrep);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContestProblemResrep> update(@PathVariable Integer id,
            @Valid @RequestBody ContestProblemResrep resrep) {
        return ResponseEntity.ok(contestProblemService.update(id, resrep));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ContestProblemResrep> partialUpdate(@PathVariable Integer id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(contestProblemService.partialUpdate(id, updates));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        contestProblemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Extra method: Search problems by difficulty.
    @GetMapping("/search")
    public ResponseEntity<List<ContestProblemResrep>> searchByDifficulty(@RequestParam String difficulty) {
        return ResponseEntity.ok(contestProblemService.searchByDifficulty(difficulty));
    }
}
