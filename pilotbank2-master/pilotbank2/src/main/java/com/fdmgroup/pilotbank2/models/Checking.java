package com.fdmgroup.pilotbank2.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.*;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Checking extends Account {

	@Column(name = "MONTHLY_TRANS_AMOUNT")
	@Builder.Default
	public static int monthlyTransactionAmount = CHECKING_MONTHLY_TRANSACTION_LIMIT;

	@Column(name = "MON_TRANS_REMAINING")
	private int monthlyTransactionsRemaining;

	@Column(name = "TRANSACTION_FEE")
	@Builder.Default
	private BigDecimal transactionFee = CHECKING_TRANSACTION_FEE;

	@Column(name = "MONTHLY_SRVC_FEE_FL")
	@Builder.Default
	private Boolean  hasMonthlyServiceFee = false;

	@Column(name = "MONTHLY_SRVC_FEE")
	@Builder.Default
	private BigDecimal monthlyServiceFee = CHECKING_MONTHLY_FEE;

	@Column(name = "MONTHLY_MIN_BAL")
	@Builder.Default
	private BigDecimal monthlyMinimumBalance = CHECKING_MINIMUM_BALANCE;

	@Column(name = "E_TRANSFER_FEE")
	@Builder.Default
	private BigDecimal eTransferFee = CHECKING_E_TRANSFER_FEE;

	@Override
	public String toString() {
		return super.toString() + " Checking [" +
				"monthlyTransactionsRemaining=" + monthlyTransactionsRemaining +
				", transactionFee=" + transactionFee +
				", hasMonthlyServiceFee=" + hasMonthlyServiceFee +
				", monthlyServiceFee=" + monthlyServiceFee +
				", monthlyMinimumBalance=" + monthlyMinimumBalance +
				", eTransferFee=" + eTransferFee +
				"]";
	}
}
