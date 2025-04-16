package com.shaastra.management.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.shaastra.management.entities.ContestProblem;
import com.shaastra.management.handler.ContestProblemService;
import com.shaastra.management.repositories.ContestProblemRepository;
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
    @Autowired
    private ContestProblemRepository contestProblemRepository;
    @Autowired
    private ModelMapper modelMapper;  // Ensure this bean is configured
    
    @GetMapping("/by-contest/{contestId}")
    public ResponseEntity<List<ContestProblemResrep>> getProblemsByContestId(@PathVariable Integer contestId) {
        List<ContestProblem> problems = contestProblemRepository.findByContestId(contestId);
        List<ContestProblemResrep> resrepList = problems.stream()
            .map(problem -> modelMapper.map(problem, ContestProblemResrep.class))
            .collect(Collectors.toList());
        return ResponseEntity.ok(resrepList);
    }
    // Extra method: Search problems by difficulty.
    @GetMapping("/search")
    public ResponseEntity<List<ContestProblemResrep>> searchByDifficulty(@RequestParam String difficulty) {
        return ResponseEntity.ok(contestProblemService.searchByDifficulty(difficulty));
    }
}
