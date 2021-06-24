package com.fdmgroup.pilotbank2.controllers;

import com.fdmgroup.pilotbank2.models.Account;
import com.fdmgroup.pilotbank2.models.Transaction;
import com.fdmgroup.pilotbank2.models.dto.TransactionCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.TransferRequestDTO;
import com.fdmgroup.pilotbank2.repo.AccountRepo;
import com.fdmgroup.pilotbank2.repo.TransactionRepo;
import com.fdmgroup.pilotbank2.services.TransactionService;
import com.fdmgroup.pilotbank2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/transactions")
public class TransactionController {

	@Autowired
	private UserService userService;

	@Autowired
	private AccountRepo accountRepository;

	@Autowired
	private TransactionRepo transactionRepository;

	@Autowired
	private TransactionService transactionService;

	@GetMapping("/")
	@SecurityRequirement(name = "jwt-bearer")
	public ResponseEntity<List<Transaction>> getTransactions(Long accountId) {
		List<Transaction> transactions = accountRepository.findById(accountId)
				.orElseThrow(() -> new IllegalArgumentException(String.format("Account with ID: %s not found", accountId)))
				.getAllTransactions();
		return new ResponseEntity<>(transactions, HttpStatus.OK);
	}

	@GetMapping("/statementList")
	@SecurityRequirement(name = "jwt-bearer")
	public List<ArrayList<Transaction>> getStatementList(Long accountId) {

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new IllegalArgumentException(String.format("Account with ID: %s not found", accountId)));
		List<Transaction> transactions = account.getAllTransactions();

		transactions.sort((obj1, obj2) -> obj1.getTransactionDate().compareTo(obj2.getTransactionDate()));

		// a list to hold all the monthly statements
		List<ArrayList<Transaction>> statementList = new ArrayList<ArrayList<Transaction>>();
		// a list to hold a single month statement
		ArrayList<Transaction> singleMonthlyStatement = new ArrayList<>();

		YearMonth curYearMonth = YearMonth.from( account.getAccountCreationDate() );

		if(transactions.size() > 0) {
			for(Transaction transaction : transactions) {
				if(YearMonth.from(transaction.getTransactionDate()).equals(curYearMonth)) {
					singleMonthlyStatement.add(transaction);
				} else {
					statementList.add(singleMonthlyStatement);
					singleMonthlyStatement = new ArrayList<>();

					curYearMonth = curYearMonth.plusMonths(1L);
					while(!YearMonth.from(transaction.getTransactionDate()).equals(curYearMonth)) {
						statementList.add(singleMonthlyStatement);
						singleMonthlyStatement = new ArrayList<>();
						curYearMonth = curYearMonth.plusMonths(1L);
					}
					singleMonthlyStatement.add(transaction);
				}
			}
			statementList.add(singleMonthlyStatement);
			singleMonthlyStatement = new ArrayList<>();
		}

		if(statementList.size() > 0) {
			curYearMonth = curYearMonth.plusMonths(1L);
		}
		while(curYearMonth.compareTo(YearMonth.from(LocalDate.now())) <= 0) {
			statementList.add(singleMonthlyStatement);
			singleMonthlyStatement = new ArrayList<>();
			curYearMonth = curYearMonth.plusMonths(1L);
		}
		if(statementList.size() > 0) {
			statementList.remove(statementList.size()-1);
			Collections.reverse(statementList);
		}
		return statementList;
	}

	@GetMapping("/statement")
	@SecurityRequirement(name = "jwt-bearer")
	public List<Transaction> getStatement(Long accountId, String month, String year) {
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new IllegalArgumentException(String.format("Account with ID: %s not found", accountId)));

		Integer monthVal = Integer.parseInt(month);
		Integer yearVal = Integer.parseInt(year);

		if(monthVal == null || monthVal < 1 || monthVal > 12) {
			throw new NumberFormatException("Invalid month value. The value must be between '1' and '12' (both inclusive).");
		}
		if(yearVal == null || yearVal < 0) {
			throw new NumberFormatException("Invalid year value. The value must be greater than or equal to '0'.");
		}

		YearMonth yearMonth = YearMonth.of( yearVal, monthVal );
		LocalDate firstOfMonth = yearMonth.atDay(1 );
		LocalDateTime monthStart = firstOfMonth.atStartOfDay();
		LocalDate last = yearMonth.atEndOfMonth();
		LocalDateTime monthEnd = last.atTime(23, 59, 59, 999999999);

		List<Transaction> statement = transactionRepository.findMonthlyStatementByAccount(account, monthStart, monthEnd);
		if(statement == null) {
			throw new IllegalArgumentException("Unable to find a transaction for the account with the specified month and year.");
		}

		return statement;
	}

	/***
	 *  An endpoint that creates a transaction. The balance on the account will be updated
	 *  based on the transactionType (Gender) of the transaction.
	 *
	 * @param transactionRequest
	 * @return Transaction.class
	 */
	@PostMapping("/create")
	@SecurityRequirement(name = "jwt-bearer")
	public ResponseEntity<?> createTransaction(@RequestBody TransactionCreationDTO transactionRequest) {
		try {
			Transaction transaction = transactionService.createTransaction(transactionRequest);
			return new ResponseEntity<>(transaction, HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			String message = String.format("Transaction could not be created for Account ID: %s. Error: %s.",
					transactionRequest.getAccountId(), e);
			return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@PutMapping("/transfer")
	@SecurityRequirement(name = "jwt-bearer")
	public ResponseEntity<?> transferFunds(@RequestBody TransferRequestDTO transferRequest) {
		try {
			//Transaction transaction = transactionService.transferFunds(transferRequest);
			List<Account> transaction = transactionService.transferFunds(transferRequest);
			return new ResponseEntity<>(transaction, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			String message = String.format("Funds Transfer failed: %s", e);
			return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

}
