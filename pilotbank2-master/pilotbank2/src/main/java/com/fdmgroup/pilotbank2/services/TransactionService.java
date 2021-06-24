package com.fdmgroup.pilotbank2.services;

import com.fdmgroup.pilotbank2.models.Account;
import com.fdmgroup.pilotbank2.models.Transaction;
import com.fdmgroup.pilotbank2.models.dto.TransactionCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.TransferRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface TransactionService {

	List<Transaction> findTransactionsByAccount(Account account);
	Transaction createTransaction(TransactionCreationDTO transactionRequest);
	List<Account> transferFunds(TransferRequestDTO transferRequest);
}
