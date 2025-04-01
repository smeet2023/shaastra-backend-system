package com.shaastra.management.resource_representation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentsResrep {
    private long erp_id;
    private Integer current_year;
    private String division;
    private String batch;
    private String sh_id;
    private String tp_id;
    private String personal_email;
    private String gsuite_email;
    private String phone;
    private String branch;
}
