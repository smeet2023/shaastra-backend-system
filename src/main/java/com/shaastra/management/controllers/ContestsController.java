package com.shaastra.management.controllers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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

import com.shaastra.management.handler.ContestsService;
import com.shaastra.management.resource_representation.ContestsResrep;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ContestsController {

    private final ContestsService contestsService;
    @Autowired
    private PagedResourcesAssembler<ContestsResrep> pagedAssembler;
    
    
    @GetMapping
    public ResponseEntity<List<ContestsResrep>> getAll() {
        return ResponseEntity.ok(contestsService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContestsResrep> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(contestsService.getById(id));
    }
    @GetMapping("/recent/participants-summary")
    public ResponseEntity<Map<String, Integer>> getParticipationSummary() {
        Map<String, Integer> summary = contestsService.getRecentParticipationSummary();
        return ResponseEntity.ok(summary);
    }
    
    @PostMapping("/create-contest")
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        contestsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Extra method: Get upcoming contests (i.e. contests with a contest_date in the future)
    @GetMapping("/upcoming")
    public ResponseEntity<PagedModel<EntityModel<ContestsResrep>>> getUpcomingContests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "contest_date,asc") String[] sort) {

        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        OffsetDateTime now = OffsetDateTime.now();
        Page<ContestsResrep> contestsPage = contestsService.getUpcomingContests(now, pageable);
        
        PagedModel<EntityModel<ContestsResrep>> pagedModel = pagedAssembler.toModel(contestsPage);
        return ResponseEntity.ok(pagedModel);
    }
}
