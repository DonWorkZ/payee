package com.fdmgroup.pilotbank2.services;

import com.fdmgroup.pilotbank2.models.*;
import com.fdmgroup.pilotbank2.models.dto.AccountCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.MainAccountRequestDTO;
import com.fdmgroup.pilotbank2.repo.AccountRepo;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import com.fdmgroup.pilotbank2.type.TransactionMemoEnum;
import com.fdmgroup.pilotbank2.type.TransactionTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.*;

@Service
public class AccountServiceImpl implements AccountService {
	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private UserRepo userRepo;

	@Override
	public <A extends Account> A findAccountById(Long accountId) {
		return (A) accountRepo.findByAccountId(accountId)
				.orElseThrow(()-> new IllegalArgumentException(String.format("Account with ID %s not found", accountId)));
	}

	@Override
	public <A extends Account> A setAsMainAccount(MainAccountRequestDTO mainAccountRequest) {
		Account oldAccount = accountRepo.findByAccountId(mainAccountRequest.getOldAccountId())
				.orElseThrow(()-> new IllegalStateException(String.format("Previous main account with ID %s not found",
						mainAccountRequest.getOldAccountId())));
		Account newAccount = accountRepo.findByAccountId(mainAccountRequest.getNewAccountId())
				.orElseThrow(()-> new IllegalStateException(String.format("New main account with ID %s not found",
				mainAccountRequest.getNewAccountId())));
		Customer customer = (Customer) userRepo.findByUserId(oldAccount.getOpenedByCustomer().getUserId())
				.orElseThrow(() -> new IllegalStateException(String.format("Customer with ID: %s not found",
						oldAccount.getOpenedByCustomer().getUserId())));

		customer.setMainAccount(newAccount);
		oldAccount.setIsMainAccount(false);
		newAccount.setIsMainAccount(true);
		userRepo.saveAndFlush(customer);
		return (A) newAccount;
	}

	@Override
	public List<Account> findAccountByCustomer(Customer customer) {
		return accountRepo.findByOwnedAccountCustomer(customer);
	}

	@Override
	public List<Account> findAll() {
		return accountRepo.findAll();
	}

	private Account accountActual;

	@Override
	public Account createAccount(AccountCreationDTO account) throws IllegalArgumentException {
		Customer customerActual = (Customer) userRepo.findById(account.getOpenedByCustomerId())
				.orElseThrow(() -> new IllegalArgumentException(String.format("Customer with ID %s not found", account.getOpenedByCustomerId())));

		switch (account.getAccountType()) {
			case "CHECKING":
				accountActual = Checking.builder()
						.accountCreationDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
						.accountType(AccountTypeEnum.CHECKING)
						.ownedAccountCustomer(customerActual)
						.isMainAccount(account.getIsMainAccount())
						.monthlyTransactionsRemaining(Checking.monthlyTransactionAmount)
						.build();
				break;
			case "FIRST CLASS CHEQUING":
				accountActual = FirstClassChecking.builder()
						.accountCreationDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
						.accountType(AccountTypeEnum.FIRST_CLASS_CHECKING)
						.ownedAccountCustomer(customerActual)
						.isMainAccount(account.getIsMainAccount())
						.build();
				break;
			case "SAVINGS":
				accountActual = Savings.builder()
						.accountCreationDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
						.accountType(AccountTypeEnum.SAVINGS)
						.ownedAccountCustomer(customerActual)
						.isMainAccount(account.getIsMainAccount())
						.build();
				break;
			case "STUDENT":
				accountActual = Student.builder()
						.accountCreationDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
						.accountType(AccountTypeEnum.STUDENT)
						.ownedAccountCustomer(customerActual)
						.isMainAccount(account.getIsMainAccount())
						.build();
				break;
			case "BUSINESS VISA":
				accountActual = BusinessVisa.builder()
						.accountCreationDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
						.accountType(AccountTypeEnum.BUSINESS_VISA)
						.balance(BigDecimal.valueOf(0))
						.ownedAccountCustomer(customerActual)
						.isMainAccount(account.getIsMainAccount())
						.creditLimit(BUSINESS_VISA_CREDIT_LIMIT)
						.build();
				break;
			case "PREMIUM VISA":
				accountActual = PremiumVisa.builder()
						.accountCreationDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
						.accountType(AccountTypeEnum.PREMIUM_VISA)
						.balance(BigDecimal.valueOf(0))
						.ownedAccountCustomer(customerActual)
						.isMainAccount(account.getIsMainAccount())
						.creditLimit(PREMIUM_VISA_CREDIT_LIMIT)
						.build();
				break;
			default:
				throw new IllegalArgumentException(String.format("Account type %s not defined", account.getAccountType()));
		}

		Transaction transaction = account.getAccountType().equals("PREMIUM VISA") ||
								account.getAccountType().equals("BUSINESS VISA") ?
								null :
								Transaction.builder()
									.account(accountActual)
									.transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
									.amount(account.getBalance())
									.transactionType(TransactionTypeEnum.CREDIT)
									.transactionMemo(TransactionMemoEnum.INITIAL_DEPOSIT)
									.build();

		createAccountNumber(accountActual);
		customerActual.addToOwnedAccounts(accountActual);
		if(!account.getAccountType().equals("PREMIUM VISA") && !account.getAccountType().equals("BUSINESS VISA")) {
			accountActual.addTransaction(transaction);
			accountActual.updateBalance(transaction);
		}

		accountRepo.saveAndFlush(accountActual);
		return accountActual;
	}

	private void createAccountNumber(Account account){
		int accountNumber = (int)(Math.random()*999999999);
		if (accountNumber < 10000000){
			accountNumber += 10000000;
		}
		System.out.println("here " + accountNumber);
		account.setAccountNumber(accountNumber);

	}
}
