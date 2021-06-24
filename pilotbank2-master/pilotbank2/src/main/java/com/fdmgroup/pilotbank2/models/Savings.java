package com.fdmgroup.pilotbank2.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.SAVINGS_INTEREST_RATE;
import static com.fdmgroup.pilotbank2.common.PilotBankConstants.SAVINGS_MINIMUM_BALANCE;
import static com.fdmgroup.pilotbank2.type.TransactionMemoEnum.MINIMUM_BALANCE_CHARGE;

@Entity
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Savings extends Account {

	@Column(name = "MIN_BALANCE", nullable = false)
	@Builder.Default
	private BigDecimal minBalance = SAVINGS_MINIMUM_BALANCE;

	@Column(name = "MIN_BALANCE_CHARGE")
	@Builder.Default
	private BigDecimal minBalanceCharge = MINIMUM_BALANCE_CHARGE.getTransactionFee();

	@Column(name = "INTEREST_RATE", nullable = false)
	@Builder.Default
	private BigDecimal interestRate = SAVINGS_INTEREST_RATE;

	@Override
	public String toString() {
		return super.toString() + " Savings [minBalance=" + minBalance + ", minBalanceCharge=" + minBalanceCharge + ", interestRate="
				+ interestRate + "]";
	}

}
