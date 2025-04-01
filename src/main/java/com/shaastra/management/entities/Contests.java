package com.shaastra.management.entities;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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
public class Contests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contestId;

    private Integer total_participants;
    private String status;
    private String contest_description; // could be part of an HTML page
    
    @Column(unique = true)
    private String contest_link;
    
    private OffsetDateTime contest_date;
    
    @ManyToMany
    @JoinTable(
        name = "contest_contest_participant_join_table",
        joinColumns = @JoinColumn(name = "contest_id"),
        // Corrected inverse join column to reference the primary key of ContestParticipants
        inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    @JsonManagedReference
    @JsonIgnoreProperties({"contestResults", "student", "solvedProblems", "contests"})
    private Set<ContestParticipants> participants = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "contest_contest_problem_join_table",
        joinColumns = @JoinColumn(name = "contest_id"),
        inverseJoinColumns = @JoinColumn(name = "contest_problem_id")
    )
    @JsonManagedReference
    private Set<ContestProblem> contestProblems;
    
    // Getters and setters ...
}
