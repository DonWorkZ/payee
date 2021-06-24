package com.fdmgroup.pilotbank2.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayeeCreationDTO {

	private String companyName;
	private String postalCode;
	private String accountNumber;
	private String nickname;
	private Long customerId;

}
