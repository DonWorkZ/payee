package com.fdmgroup.pilotbank2.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionTypeEnum {
	DEBIT ("Debit"),
	CREDIT ("Credit");

	private final String transactionTypeName;
}
