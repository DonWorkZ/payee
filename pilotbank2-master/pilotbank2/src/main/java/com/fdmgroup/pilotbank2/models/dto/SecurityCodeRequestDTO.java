package com.fdmgroup.pilotbank2.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityCodeRequestDTO {
    private String username;
    //private String emailOrText;
    //private String contactInfo;
}
