package com.fdmgroup.pilotbank2.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreationDTO {
	private Long openedByCustomerId;
	private BigDecimal balance;
	private String accountType;
	private Boolean isMainAccount;
}
