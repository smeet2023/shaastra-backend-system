package com.shaastra.management.resource_representation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolvedProblemsPostResRep {
    private Integer contest_participant_id;
    private Integer contest_id;
    private Integer contest_problem_id;
    private Integer score;
}
