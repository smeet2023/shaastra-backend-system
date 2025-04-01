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
public class ContestParticipantsGetResrep {
    private Integer participant_id;      // Unique participant identifier
    private String sh_id;                // Student's unique identifier
    private String studentName;          // Full name from the associated Student entity
    private String personal_email;       // Email address (for display/contact)
    private Set<Integer> contestIds;     // List of contest IDs the participant is registered in
}
