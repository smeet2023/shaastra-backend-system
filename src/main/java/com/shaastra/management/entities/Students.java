package com.shaastra.management.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"erp_id", "tp_id", "personal_email", "gsuite_email"})
})
public class Students {

    @Id
    @NotNull(message = "!! -> erp_id cannot be empty")
    private long erp_id; // e.g., 10322
    
    @Column(nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer current_year;
    
    @Column(nullable = false)
    private String division;
    
    @Column(nullable = false)
    private String batch;
    
    @Column(unique = true, nullable = false)  // Now non-null
    private String sh_id;
    
    @Column(nullable = false)
    private String tp_id;
    
    @Column(nullable = false, length = 70)
    private String personal_email;
    
    @Column(nullable = false, length = 70)
    private String gsuite_email;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(nullable = false)
    private String branch;
    
    // Getters and setters...
}

