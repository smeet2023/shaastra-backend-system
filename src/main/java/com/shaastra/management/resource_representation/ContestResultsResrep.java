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
    private Integer contest_id;               // ID of the contest
    private String contest_name;              // Name of the contest for display
    private Integer participant_id;           // ID of the participant
    private String participant_name;          // Participant's full name for display
    private Integer score;                    // Score achieved by the participant
    private Integer rank_in_this_contest;     // Rank within the contest
    private String status;                    // Result status, e.g., result-confirmed
}
