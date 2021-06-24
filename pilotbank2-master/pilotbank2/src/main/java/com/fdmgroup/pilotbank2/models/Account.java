package com.fdmgroup.pilotbank2.models;

import com.fasterxml.jackson.annotation.*;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import com.fdmgroup.pilotbank2.type.TransactionTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.ISO_DATE_TIME;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Account")
@JsonTypeInfo(
		use = JsonTypeInfo.Id.CLASS,
		include = JsonTypeInfo.As.PROPERTY,
		property = "accountType")
@JsonSubTypes({
		@JsonSubTypes.Type(value = Checking.class, name = "CHECKING"),
		@JsonSubTypes.Type(value = Savings.class, name = "SAVINGS")
})
public abstract class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acc_id_sequence")
	@SequenceGenerator(name = "acc_id_sequence", sequenceName = "account_pk_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ACC_ID", nullable = false)
	private Long accountId;

	@Column(name = "date_time")
	private LocalDateTime accountCreationDate;

	@OneToOne(mappedBy = "mainAccount", optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "OPENED_BY_CUST_ID")
	@JsonBackReference(value = "customer-to-main-account")
	private Customer openedByCustomer;

	@ManyToOne
	@JsonProperty(access= JsonProperty.Access.WRITE_ONLY)
	@JoinColumn(name = "OWNING_CUST_ID")
	@JsonBackReference(value = "customer-to-owned-accounts")
	private Customer ownedAccountCustomer;

	@Column(name = "account_balance")
	@Builder.Default
	private BigDecimal balance = BigDecimal.ZERO;

	@Column(name = "Account_Type")
	@Enumerated(EnumType.STRING)
	private AccountTypeEnum accountType;

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@OrderBy("transactionDate DESC")
	@JsonManagedReference(value = "account-to-transactions")
	@Builder.Default
	private List<Transaction> allTransactions = new ArrayList<>();

	@Column(name = "MAIN_ACCOUNT_FL", columnDefinition = "tinyint(1)")
	private Boolean isMainAccount;

	@Column(name = "ACCOUNT_NUMBER")
	private int accountNumber = 0;

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", balance=" + balance +
				", accountCreationDate=" + accountCreationDate + ", accountType="
				+ accountType + "]";
	}

	public void addTransaction(Transaction transaction) {
		this.allTransactions.add(transaction);
		transaction.setAccount(this);
	}

	public void reverseTransaction(Transaction transaction){
		Transaction reversedTransaction = Transaction.builder()
				.amount(transaction.getAmount())
				.transactionType(reverseGender(transaction.getTransactionType()))
				.transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
				.account(transaction.getAccount())
				.build();
		this.addTransaction(reversedTransaction);
	}

	private TransactionTypeEnum reverseGender(TransactionTypeEnum transactionType) {
		return transactionType.equals(TransactionTypeEnum.CREDIT) ?
				TransactionTypeEnum.DEBIT : TransactionTypeEnum.CREDIT;
	}

	public void updateBalance(Transaction transaction){
		this.balance = transaction.getTransactionType().equals(TransactionTypeEnum.CREDIT)
				? this.balance.add(transaction.getAmount())
				: this.balance.subtract(transaction.getAmount());
	}

}
