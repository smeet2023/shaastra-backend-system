package com.shaastra.management.entities;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ContestProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contest_problem_id;
    @ManyToMany(mappedBy = "contestProblems")
    @JsonBackReference
    private Set<Contests> contests;
    @Column(nullable = false, unique = true)
    private String problem_title;
    @Column(nullable = false)
    private String problem_description;
    @Column( unique = true)
    private String problem_solution;
    @Column(nullable = false)
    private String problem_difficulty;
}