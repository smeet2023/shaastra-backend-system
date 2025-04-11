package com.shaastra.management.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ContestResults {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contests contest;
    // Correct the join column to refer to ContestParticipants' primary key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private ContestParticipants contestParticipant;
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer score;
    private Integer rank_in_this_contest;
    private String status; // result-invalid, result-confirmed, result-not-set
    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = "result-not-set";  // Set default value if not provided
        }
    }
}