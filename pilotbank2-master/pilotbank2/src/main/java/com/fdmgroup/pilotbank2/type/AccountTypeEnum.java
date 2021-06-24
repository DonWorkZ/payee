package com.fdmgroup.pilotbank2.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountTypeEnum {

	SAVINGS ("Savings"),
	CHECKING ("Checking"),
	STUDENT ("Student"),
	FIRST_CLASS_CHECKING ("First Class Checking"),
	BUSINESS_VISA ("Business Visa"),
	PREMIUM_VISA("Premium Visa");

	private final String accountTypeName;
	
}
