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
    // these fields are set by business logic or are calculated later:
    private Integer total_participants;
    private String status;
    
    // Input fields:
    private String contest_description;
    private String contest_link;
    private OffsetDateTime contest_date;
    
    // List of ContestProblem IDs provided during creation.
    private Set<Integer> contestProblemIds;
}

