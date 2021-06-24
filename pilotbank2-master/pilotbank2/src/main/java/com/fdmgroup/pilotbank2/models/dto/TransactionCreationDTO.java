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
public class TransactionCreationDTO {
	private BigDecimal amount;
	private String transactionType;
	private String transactionMemo;
	private Long accountId;
	private Long payeeId;
}
