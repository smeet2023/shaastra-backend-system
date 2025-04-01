package com.shaastra.management.resource_representation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestProblemResrep {
    private Integer contest_problem_id;  // Unique identifier for the problem
    private String problem_title;
    private String problem_description;
    private String problem_solution;
    private String problem_difficulty;
    
}
