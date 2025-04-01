package com.shaastra.management.resource_representation;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestParticipantsPatchResrep {
    private Set<Integer> addContestIds;      // Contests to add
    private Set<Integer> removeContestIds;   // Contests to remove
}
