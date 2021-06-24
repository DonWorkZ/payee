package com.fdmgroup.pilotbank2.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "PAYEE")
public class Payee {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payeeIdSequence")
	@SequenceGenerator(name = "payeeIdSequence", sequenceName = "PAYEE_ID_SEQ", initialValue = 1, allocationSize = 1)
	@Column(name = "PAYEE_ID", nullable = false)
	private Long payeeId;

	@Column(name = "COMPANY_NAME")
	private String companyName;

	@Column(name = "POSTAL_CODE")
	private String postalCode;

	@Column(name = "ACCOUNT_NUMBER")
	private String accountNumber;

	@Column(name = "NICKNAME")
	private String nickname;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_ID")
	@JsonBackReference(value = "customer-to-payee")
	private Customer customer;

	@Column(name = "ACTIVE_FL")
	@Builder.Default
	private boolean isActive = true;

	@OneToMany(mappedBy = "payee",cascade = CascadeType.ALL)
	@Builder.Default
	@JsonManagedReference(value = "transaction-to-payee")
	private List<Transaction> transactions = new ArrayList<>();

	@Override
	public String toString() {
		return "Payee: [payeeId=" + payeeId + "companyName=" + companyName + "postalCode=" + postalCode + "accountNumber=" + accountNumber
				+ "nickname=" + nickname + "isActive=" + isActive + "]";
	}
}
