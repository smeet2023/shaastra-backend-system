package com.shaastra.management.resource_representation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AuthResponse {
    private String jwt;
    private String message;

}
