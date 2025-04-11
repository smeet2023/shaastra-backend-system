package com.shaastra.management.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class SolvedProblems {
    @Id    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sp_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_participant_id", nullable = false)
    @JsonBackReference
    private ContestParticipants contestParticipant;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    @JsonIgnore
    private Contests contest;
    @ManyToOne
    @JoinColumn(name = "contest_problem_id", nullable = false)
    private ContestProblem contestProblem;
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer score;
    @JsonProperty("contest_id")
    public Integer getContestId() {
        return this.contest != null ? this.contest.getContestId() : null;
    }
}