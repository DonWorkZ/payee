package com.fdmgroup.pilotbank2.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "USR_TABLE")
@JsonTypeInfo(
		use = JsonTypeInfo.Id.CLASS,
		include = JsonTypeInfo.As.PROPERTY,
		property = "userType")
@JsonSubTypes({
		@JsonSubTypes.Type(value = Customer.class, name = "CUSTOMER"),
		@JsonSubTypes.Type(value = Admin.class, name ="ADMIN")
})
public abstract class User {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usr_id_sequence")
	@SequenceGenerator(name = "usr_id_sequence", sequenceName = "user_pk_seq", initialValue = 3, allocationSize = 1)
	@Column(name = "USR_ID", nullable = false)
	private Long userId;

	@Column(name = "USER_NAME", unique = true)
	private String username;

	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;

	@Column(nullable = false)
	private String email;

	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;

	@Column(name = "USER_PASSWORD")
	@JsonProperty(access= JsonProperty.Access.WRITE_ONLY)
	private String password;

	@Column(name = "PASSWORD_EXPIRES_DT")
	@Builder.Default
	private LocalDateTime passwordExpires = LocalDateTime.now().plusDays(90L);

	@Column (nullable = false)
	private String role;

	@Column(name = "ACCOUNT_ACTIVE_FL", nullable = false)
	private Boolean isActive;

	@Column(name = "LAST_FAILED_LOGIN_DT")
	private LocalDateTime lastFailedLoginDate;

	@Column(name = "FAILED_LOGIN_COUNT")
	private int failedLoginCount = 0;

	@Column(name = "ACCOUNT_LOCKED_FL", columnDefinition = "tinyInt(1)")
	private Boolean accountLockedFlag;

	@Column(name = "ACCOUNT_EXPIRES_DT")
	private LocalDateTime accountExpires;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String sin;

	@Column(nullable = false)
	private String occupation;

	private String industry;
	private String title;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference(value = "user-to-address")
	@Builder.Default
	private List<Address> addressList = new ArrayList<>();

	@Column(name = "SECURITY_QUESTION")
	private String securityQuestion;

	@Column(name = "SECURITY_ANSWER")
	private String securityAnswer;

	@Column(name = "TEMP_LOCK_OUT_EXP")
	private LocalDateTime tempLockOutExpiration;

	@Column(name = "INCORRECT_ANSWER_COUNT")
	private int incorrectAnswerCount = 0;

	@Column(name = "CORRECT_ANSWER_FL")
	private Boolean isAnswerCorrect = false;

	@Column(name = "SECURITY_CODE_VERIFICATION_FL")
	private Boolean isSecurityCodeVerified = false;

	@Column(name = "DEVICE_INFO")
	private String deviceInfo;

	@Column(name = "SECURITY_CODE")
	private int securityCode = 0;

	@Override
	public String toString() {
		return "User [userId=" + userId + ", title=" + title + ", username=" + username + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + ", phoneNumber=" + phoneNumber + ", sin=" + sin
				+ "role= " + role + " accountActive: " + isActive + ", accountExpires=" + accountExpires + "]";
	}

	public void addAddress(Address address){
		address.setUser(this);
		this.addressList.add(address);
	}

	public void removeAddress(Address address){
		address.setUser(null);
		this.addressList.remove(address);
	}

}