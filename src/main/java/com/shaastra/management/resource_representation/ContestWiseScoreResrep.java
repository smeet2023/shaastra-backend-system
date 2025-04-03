package com.shaastra.management.resource_representation;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestWiseScoreResrep {
    private String sh_id;                    // Participant's student ID (from Student)
    private String contest_name;             // Friendly contest name
    private OffsetDateTime contest_date;     // Contest date/time
    private Integer score;                   // Score from ContestResults table
    private Integer rank_in_this_contest;    // Rank from ContestResults table
    private String status;                   // Result status (e.g., result-confirmed)
}
