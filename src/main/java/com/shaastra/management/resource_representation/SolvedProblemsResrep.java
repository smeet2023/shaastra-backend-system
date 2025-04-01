package com.shaastra.management.resource_representation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolvedProblemsResrep {
    private Integer solved_problem_id;       // Unique identifier for the solved problem record
    private Integer contest_participant_id;    // ID of the participant who solved the problem
    private String participantName;            // Name of the participant (from ContestParticipants/Student)
    private Integer contest_id;                // The contest in which the problem was solved
    private String contestName;                // A friendly name or title for the contest
    private Integer contest_problem_id;        // The problem that was solved
    private String problem_title;              // Title of the solved problem for quick reference
    private Integer score;                     // Score awarded for solving the problem
}
