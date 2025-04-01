package com.shaastra.management.resource_representation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestResultsResrep {
    private Integer contest_id;
    private Integer participant_id;
    private Integer score;
    private Integer rank_in_this_contest;
    private String status;
}
