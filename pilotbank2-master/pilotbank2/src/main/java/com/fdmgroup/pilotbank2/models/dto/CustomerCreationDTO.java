package com.fdmgroup.pilotbank2.models.dto;

import com.fdmgroup.pilotbank2.models.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCreationDTO {

	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String password;
	private String role;
	private Boolean isActive;
	private String sin;
	private String occupation;
	private String industry;
	private BigDecimal income;
	private String title;
	private Address address;
	private String securityQuestion;
	private String securityAnswer;
	private AccountCreationDTO account;
}
