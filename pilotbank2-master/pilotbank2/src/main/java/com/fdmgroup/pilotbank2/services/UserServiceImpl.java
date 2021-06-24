package com.fdmgroup.pilotbank2.services;

import com.fdmgroup.pilotbank2.common.PilotBankConstants;
import com.fdmgroup.pilotbank2.models.*;
import com.fdmgroup.pilotbank2.models.dto.*;
import com.fdmgroup.pilotbank2.repo.AccountRepo;
import com.fdmgroup.pilotbank2.repo.AddressRepo;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import com.fdmgroup.pilotbank2.type.TransactionMemoEnum;
import com.fdmgroup.pilotbank2.type.TransactionTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.ISO_DATE_TIME;
import static com.fdmgroup.pilotbank2.common.PilotBankConstants.PASSWORD_ERROR_MESSAGE;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private AddressRepo addressRepo;

	private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	private Account accountActual;

	@Override
	public boolean checkUniqueUser(String userName, String email) {
		return userRepo.findByUsernameAndEmail(userName, email);
	}

	@Override
	public boolean checkUniqueUsername(String username) {
		return userRepo.existsByUsername(username);
	}

	@Override
	public Customer findCustByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public List<User> findAll() {
		return userRepo.findAll();
	}

	@Override
	public User findById(Long userId) {
		User user = userRepo.findByUserId(userId)
				.orElseThrow(() -> new IllegalStateException(String.format("User with ID: %s not found", userId)));
		return user;
	}

	@Override
	public User validateUser(String email, String password) {
		return userRepo.findByEmailAndPassword(email, password);
	}

	@Override
	public Customer createCustomer(CustomerCreationDTO incomingCustomer) throws IllegalArgumentException {
		Customer customerActual = buildActualCustomer(incomingCustomer);
		validatePassword(incomingCustomer, customerActual);
		createAccountFromAccountType(incomingCustomer, customerActual);
		Address address = buildAddress(incomingCustomer);

		Transaction transaction = incomingCustomer.getAccount().getAccountType().equals("PREMIUM VISA") ||
								incomingCustomer.getAccount().getAccountType().equals("PREMIUM VISA") ?
								null : buildInitialTransaction(incomingCustomer);

		customerActual.addAddress(address);
		if(!incomingCustomer.getAccount().getAccountType().equals("PREMIUM VISA") &&
			!incomingCustomer.getAccount().getAccountType().equals("PREMIUM VISA")) {
				accountActual.addTransaction(transaction);
		}
		userRepo.saveAndFlush((User) customerActual);

		return customerActual;
	}

	@Override
	public <U extends User> U updateUser(Long userId, UserUpdateDTO userUpdate) {
		User userActual = userRepo.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException(String.format("User with userId %s not found!", userId)));
		updateUserInformation(userUpdate, userActual);
		updateUserAddress(userUpdate, userActual);
		return (U) userRepo.saveAndFlush(userActual);
	}

	@Override
	public <U extends User> U deleteUser(Long userId) {
		User userActual = userRepo.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException(String.format("User with userId %s not found!", userId)));
		userActual.setIsActive(false);
		return (U) userRepo.saveAndFlush(userActual);
	}


	private Customer buildActualCustomer(CustomerCreationDTO incomingCustomer) {
		Customer customerActual = new Customer();
		customerActual.setTitle(incomingCustomer.getTitle());
		customerActual.setFirstName(incomingCustomer.getFirstName());
		customerActual.setLastName(incomingCustomer.getLastName());
		customerActual.setPhoneNumber(incomingCustomer.getPhoneNumber());
		customerActual.setUsername(incomingCustomer.getUsername());
		customerActual.setEmail(incomingCustomer.getEmail());
		customerActual.setSin(passwordEncoder.encode(incomingCustomer.getSin()));
		customerActual.setOccupation(incomingCustomer.getOccupation());
		customerActual.setIndustry(incomingCustomer.getIndustry());
		customerActual.setIncome(incomingCustomer.getIncome());
		customerActual.setRole(incomingCustomer.getRole());
		customerActual.setIsActive(incomingCustomer.getIsActive());
		customerActual.setAccountLockedFlag(false);
		customerActual.setSecurityQuestion(incomingCustomer.getSecurityQuestion() == null ?
				"What is your favourite food?" : incomingCustomer.getSecurityQuestion());
		customerActual.setSecurityAnswer(incomingCustomer.getSecurityAnswer() == null ?
				"pizza" : incomingCustomer.getSecurityAnswer());
		customerActual.setIsAnswerCorrect(false);
		customerActual.setIsSecurityCodeVerified(false);
		customerActual.setTempLockOutExpiration(LocalDateTime.parse("1970-01-01T00:00:00"));
		customerActual.setAddressList(new ArrayList<>());

		return customerActual;
	}

	private void validatePassword(CustomerCreationDTO incomingCustomer, Customer customerActual) {
		if (isValidPassword(incomingCustomer.getPassword())) {
			customerActual.setPassword(passwordEncoder.encode(incomingCustomer.getPassword()));
			customerActual.setPasswordExpires(LocalDateTime.now().plusDays(90L));
		} else {
			throw new IllegalArgumentException(PASSWORD_ERROR_MESSAGE);
		}
	}

	private boolean isValidPassword(String password) {
		boolean isValid = checkFieldIsNotBlankBeforeUpdate(password);
		if (isValid) {
			Pattern pattern = Pattern.compile(PilotBankConstants.PASSWORD_REGEX);
			Matcher matcher = pattern.matcher(password);
			return isValid && matcher.matches();
		}
		return false;
	}

	private void createAccountFromAccountType(CustomerCreationDTO incomingCustomer, Customer customerActual) {
		try{
			switch (incomingCustomer.getAccount().getAccountType()) {
				case "CHECKING":
					accountActual = Checking.builder()
							.accountCreationDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
							.accountType(AccountTypeEnum.CHECKING)
							.balance(incomingCustomer.getAccount().getBalance())
							.monthlyTransactionsRemaining(Checking.monthlyTransactionAmount)
							.hasMonthlyServiceFee(false)
							.isMainAccount(true)
							.build();
					addAccountsToCustomer(customerActual, accountActual);
					break;
				case "FIRST CLASS CHEQUING":
				case "FIRST_CLASS_CHECKING":
					accountActual = FirstClassChecking.builder()
							.accountCreationDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
							.accountType(AccountTypeEnum.FIRST_CLASS_CHECKING)
							.balance(incomingCustomer.getAccount().getBalance())
							.hasMonthlyServiceFee(false)
							.isMainAccount(true)
							.build();
					addAccountsToCustomer(customerActual, accountActual);
					break;
				case "SAVINGS":
					accountActual = Savings.builder()
							.accountCreationDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
							.accountType(AccountTypeEnum.SAVINGS)
							.balance(incomingCustomer.getAccount().getBalance())
							.ownedAccountCustomer(customerActual)
							.isMainAccount(true)
							.build();
					addAccountsToCustomer(customerActual, accountActual);
					break;
				case "STUDENT":
					accountActual = Student.builder()
							.accountCreationDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
							.accountType(AccountTypeEnum.STUDENT)
							.balance(incomingCustomer.getAccount().getBalance())
							.ownedAccountCustomer(customerActual)
							.isMainAccount(true)
							.build();
					addAccountsToCustomer(customerActual, accountActual);
					break;
				case "BUSINESS VISA":
				case "BUSINESS_VISA":
					accountActual = BusinessVisa.builder()
							.accountCreationDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
							.accountType(AccountTypeEnum.BUSINESS_VISA)
							.ownedAccountCustomer(customerActual)
							.isMainAccount(true)
							.build();
					addAccountsToCustomer(customerActual, accountActual);
					break;
				case "PREMIUM VISA":
				case "PREMIUM_VISA":
					accountActual = PremiumVisa.builder()
							.accountCreationDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
							.accountType(AccountTypeEnum.PREMIUM_VISA)
							.ownedAccountCustomer(customerActual)
							.isMainAccount(true)
							.build();
					addAccountsToCustomer(customerActual, accountActual);
					break;
				default:
					//More than likely, this code will never be reached
					throw new IllegalArgumentException(String.format("Account Type %s not found", incomingCustomer.getAccount().getAccountType()));

			}
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(String.format("Account Type %s not found", incomingCustomer.getAccount().getAccountType()));
		}
	}

	public void addAccountsToCustomer(Customer customerActual, Account accountActual) {
		customerActual.setMainAccount(accountActual);
		customerActual.addToOwnedAccounts(accountActual);
	}

	private Address buildAddress(CustomerCreationDTO incomingCustomer) {
		Address address = Address.builder()
				.streetName(incomingCustomer.getAddress().getStreetName())
				.streetNumber(incomingCustomer.getAddress().getStreetNumber())
				.suiteNumber(incomingCustomer.getAddress().getSuiteNumber())
				.province(incomingCustomer.getAddress().getProvince())
				.city(incomingCustomer.getAddress().getCity())
				.postalCode(incomingCustomer.getAddress().getPostalCode())
				.build();
		return address;
	}

	private Transaction buildInitialTransaction(CustomerCreationDTO incomingCustomer) {
		Transaction transaction = Transaction.builder()
				.account(accountActual)
				.transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
				.amount(incomingCustomer.getAccount().getBalance())
				.transactionType(TransactionTypeEnum.CREDIT)
				.transactionMemo(TransactionMemoEnum.INITIAL_DEPOSIT)
				.build();
		return transaction;
	}

	private void updateUserInformation(UserUpdateDTO userUpdate, User userActual) throws IllegalArgumentException {

		if (checkFieldIsNotBlankBeforeUpdate(userUpdate.getTitle())) {
			userActual.setTitle(userUpdate.getTitle());
		}

		if (checkFieldIsNotBlankBeforeUpdate(userUpdate.getEmail())) {
			userActual.setEmail(userUpdate.getEmail());
		}

		if (checkFieldIsNotBlankBeforeUpdate(userUpdate.getPhoneNumber())) {
			userActual.setPhoneNumber(userUpdate.getPhoneNumber());
		}

		if (checkFieldIsNotBlankBeforeUpdate(userUpdate.getOccupation())) {
			userActual.setOccupation(userUpdate.getOccupation());
		}

		if (checkFieldIsNotBlankBeforeUpdate(userUpdate.getIndustry())) {
			userActual.setIndustry(userUpdate.getIndustry());
		}

		if (userUpdate.getPassword() != null) {
			if (isValidPassword(userUpdate.getPassword())) {
				userActual.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
				userActual.setPasswordExpires(LocalDateTime.now().plusDays(90L));
			} else {
				throw new IllegalArgumentException(PASSWORD_ERROR_MESSAGE);
			}
		}

		if (checkFieldIsNotBlankBeforeUpdate(userUpdate.getSecurityQuestion())) {
			userActual.setSecurityQuestion(userUpdate.getSecurityQuestion());
		}

		if (checkFieldIsNotBlankBeforeUpdate(userUpdate.getSecurityAnswer())) {
			userActual.setSecurityAnswer(userUpdate.getSecurityAnswer());
		}
	}

	private void updateUserAddress(UserUpdateDTO userUpdate, User userActual) {
		if (userUpdate.getAddress() != null) {
			Address updateAddress = userUpdate.getAddress();

			if (checkFieldIsNotBlankBeforeUpdate(updateAddress.getStreetNumber())) {
				userActual.getAddressList().get(0).setStreetNumber(updateAddress.getStreetNumber());
			}

			if (checkFieldIsNotBlankBeforeUpdate(updateAddress.getStreetName())) {
				userActual.getAddressList().get(0).setStreetName(updateAddress.getStreetName());
			}

			if (checkFieldIsNotBlankBeforeUpdate(updateAddress.getSuiteNumber())) {
				userActual.getAddressList().get(0).setSuiteNumber(updateAddress.getSuiteNumber());
			}

			if (checkFieldIsNotBlankBeforeUpdate(updateAddress.getCity())) {
				userActual.getAddressList().get(0).setCity(updateAddress.getCity());
			}

			if (checkFieldIsNotBlankBeforeUpdate(updateAddress.getProvince())) {
				userActual.getAddressList().get(0).setProvince(updateAddress.getProvince());
			}

			if (checkFieldIsNotBlankBeforeUpdate(updateAddress.getPostalCode())) {
				userActual.getAddressList().get(0).setPostalCode(updateAddress.getPostalCode());
			}
		}
	}

	private boolean checkFieldIsNotBlankBeforeUpdate(String fieldToCheck) {
		return StringUtils.isNotBlank(fieldToCheck);
	}

}
