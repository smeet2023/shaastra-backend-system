package com.shaastra.management.resource_representation;

import java.time.OffsetDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestsResrep {
    private Integer contest_id;               // Unique identifier for the contest
    private String contest_name;              // Human-friendly name/title of the contest
    private Integer total_participants;       // Total number of participants in the contest
    private String status;                    // e.g., Scheduled, Ongoing, Completed
    private String contest_description;       // Detailed description or HTML content
    private String contest_link;              // URL/link to contest details or external page
    private OffsetDateTime contest_date;      // Date and time of the contest
    private Set<Integer> contestProblemIds;   // IDs of problems associated with the contest
}

