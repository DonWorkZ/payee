package com.fdmgroup.pilotbank2.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.CHECKING_MONTHLY_FEE;
import static com.fdmgroup.pilotbank2.common.PilotBankConstants.CHECKING_TRANSACTION_FEE;

@Getter
@AllArgsConstructor
public enum TransactionMemoEnum {

	GENERAL("General Transaction", BigDecimal.ZERO),
	TRANSFER("Transfer", BigDecimal.ZERO),
	INITIAL_DEPOSIT("Initial Deposit", BigDecimal.ZERO),
	ALLOWED_MONTHLY("Allowed Monthly Transaction", BigDecimal.valueOf(25)),
	OVER_MONTHLY_ALLOWED ("Overage - Monthly Allowed Transactions", BigDecimal.valueOf(2.00)),
	INTERNATIONAL_CURRENCY ("International Currency Conversion Fee", BigDecimal.valueOf(0.99)),
	OVERDRAFT ("Overdraft Fee", BigDecimal.valueOf(25.00)),
	RETURNED_CHECK ("Returned Check Fee", BigDecimal.valueOf(25.00)),
	CHECKING_MONTHLY_SERVICE("Checking Monthly Service Fee", CHECKING_MONTHLY_FEE),
	MINIMUM_BALANCE_CHARGE("Savings - Minimum Balance Fee", BigDecimal.valueOf(0.00)),
	TRANSACTION_FEE("Transaction Fee", CHECKING_TRANSACTION_FEE);

	private final String transactionMemoName;
	private final BigDecimal transactionFee;
}
