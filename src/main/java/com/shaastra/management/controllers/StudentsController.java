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

import com.shaastra.management.handler.StudentsService;
import com.shaastra.management.resource_representation.ContestsResrep;
import com.shaastra.management.resource_representation.StudentsResrep;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentsController {

    private final StudentsService studentsService;

    @GetMapping
    public ResponseEntity<List<StudentsResrep>> getAll() {
        return ResponseEntity.ok(studentsService.getAll());
    }

    @GetMapping("/{erpId}")
    public ResponseEntity<StudentsResrep> getByErpId(@PathVariable long erpId) {
        return ResponseEntity.ok(studentsService.getByErpId(erpId));
    }

    @PostMapping
    public ResponseEntity<StudentsResrep> create(@Valid @RequestBody StudentsResrep resrep) {
        StudentsResrep created = studentsService.create(resrep);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{erpId}")
    public ResponseEntity<StudentsResrep> update(@PathVariable long erpId,
            @Valid @RequestBody StudentsResrep resrep) {
        return ResponseEntity.ok(studentsService.update(erpId, resrep));
    }

    @PatchMapping("/{erpId}")
    public ResponseEntity<StudentsResrep> partialUpdate(@PathVariable long erpId,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(studentsService.partialUpdate(erpId, updates));
    }

    @DeleteMapping("/{erpId}")
    public ResponseEntity<Void> delete(@PathVariable long erpId) {
        studentsService.delete(erpId);
        return ResponseEntity.noContent().build();
    }

    // Extra method: Get the contest history for a given student.
    @GetMapping("/{erpId}/contest-history")
    public ResponseEntity<List<ContestsResrep>> getContestHistory(@PathVariable long erpId) {
        return ResponseEntity.ok(studentsService.getContestHistory(erpId));
    }
}
