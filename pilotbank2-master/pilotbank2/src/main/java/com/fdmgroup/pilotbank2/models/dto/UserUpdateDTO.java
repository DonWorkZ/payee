package com.fdmgroup.pilotbank2.models.dto;

import com.fdmgroup.pilotbank2.models.Address;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class UserUpdateDTO {

    private String title;
    private String email;
    private String phoneNumber;
    private String password;
    private String occupation;
    private String industry;
    private String securityQuestion;
    private String securityAnswer;
    private Address address;

}
