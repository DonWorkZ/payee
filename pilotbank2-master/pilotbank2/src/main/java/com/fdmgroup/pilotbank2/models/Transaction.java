package com.fdmgroup.pilotbank2.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fdmgroup.pilotbank2.type.TransactionMemoEnum;
import com.fdmgroup.pilotbank2.type.TransactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_sequence")
	@SequenceGenerator(name = "transaction_id_sequence", sequenceName = "transaction_pk_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "TRANSACTION_ID", nullable = false)
	private int transactionId;
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	private TransactionTypeEnum transactionType;

	@Enumerated(EnumType.STRING)
	private TransactionMemoEnum transactionMemo;

	@Column(name = "date_time")
	private LocalDateTime transactionDate;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "FK_ACCOUNT_ID")
	@JsonBackReference(value = "account-to-transactions")
	private Account account;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "PAYEE_ID")
	@JsonBackReference(value = "transaction-to-payee")
	private Payee payee;

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", amount=" + amount + ", transactionType="
				+ transactionType.getTransactionTypeName() + ", transactionMemo=" + transactionMemo.getTransactionMemoName()
				+ ", transactionDate=" + transactionDate.toString() + "]";
	}

	public void addPayee(Payee payee){
		this.setPayee(payee);
		payee.getTransactions().add(this);
	}

	//More than likely this method will *NEVER* be used.
	public void removePayee(Payee payee){
		this.setPayee(null);
		payee.getTransactions().remove(this);
	}

}

