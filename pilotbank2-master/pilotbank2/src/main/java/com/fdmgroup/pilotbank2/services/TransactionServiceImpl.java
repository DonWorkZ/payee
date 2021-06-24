package com.fdmgroup.pilotbank2.services;

import com.fdmgroup.pilotbank2.models.*;
import com.fdmgroup.pilotbank2.models.dto.TransactionCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.TransferRequestDTO;
import com.fdmgroup.pilotbank2.repo.AccountRepo;
import com.fdmgroup.pilotbank2.repo.PayeeRepo;
import com.fdmgroup.pilotbank2.repo.TransactionRepo;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import com.fdmgroup.pilotbank2.type.TransactionMemoEnum;
import com.fdmgroup.pilotbank2.type.TransactionTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.ISO_DATE_TIME;
import static com.fdmgroup.pilotbank2.type.AccountTypeEnum.*;
import static com.fdmgroup.pilotbank2.type.TransactionMemoEnum.TRANSFER;
import static com.fdmgroup.pilotbank2.type.TransactionTypeEnum.DEBIT;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private PayeeRepo payeeRepo;

    @Override
    public List<Transaction> findTransactionsByAccount(Account account) {
        return transactionRepo.findAllByAccountOrderByTransactionDateDesc(account);
    }

    @Override
    public Transaction createTransaction(TransactionCreationDTO transactionRequest) throws IllegalArgumentException {
        Account accountActual = accountRepo.findById(transactionRequest.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Account with ID: %s not found", transactionRequest.getAccountId())));

		if (transactionRequest.getTransactionType().equals("DEBIT")){
			fundsCheck(transactionRequest, accountActual);
		}

		Transaction transactionActual = Transaction.builder()
                .amount(transactionRequest.getAmount())
                .transactionType(TransactionTypeEnum.valueOf(transactionRequest.getTransactionType()))
				.transactionMemo(TransactionMemoEnum.valueOf(transactionRequest.getTransactionMemo()))
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                .build();

		hasPayee(transactionRequest, transactionActual);
		accountActual.addTransaction(transactionActual);
		creditCardAmountConversion(accountActual, transactionActual);
		accountActual.updateBalance(transactionActual);
		creditCardAmountConversion(accountActual, transactionActual);
		requiresTransactionFee(accountActual, transactionActual);
		applyCashBack(accountActual, transactionActual);
		transactionRepo.save(transactionActual);
		return transactionActual;
    }

	@Override
    public List<Account> transferFunds(TransferRequestDTO transferRequest) throws IllegalArgumentException {
        final LocalDateTime transactionDate = LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME);
        Account fromAccount = accountRepo.findById(transferRequest.getFromAccountId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Account with ID %s not found", transferRequest.getFromAccountId())));
        Account toAccount = accountRepo.findById(transferRequest.getToAccountId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Account with ID %s not found", transferRequest.getToAccountId())));
        Transaction transferFrom = Transaction.builder()
                .account(fromAccount)
                .transactionType(DEBIT)
		        .transactionMemo(TRANSFER)
                .transactionDate(transactionDate)
                .amount(transferRequest.getTransferAmount())
                .build();

        Transaction transferTo = Transaction.builder()
                .account(toAccount)
                .transactionType(TransactionTypeEnum.CREDIT)
		        .transactionMemo(TRANSFER)
                .transactionDate(transactionDate)
                .amount(transferRequest.getTransferAmount())
                .build();

        if (!toAccount.equals(fromAccount)) {
            if (fromAccount.getBalance().compareTo(transferRequest.getTransferAmount()) >= 0) {
                fromAccount.addTransaction(transferFrom);
                fromAccount.updateBalance(transferFrom);

                toAccount.addTransaction(transferTo);
                toAccount.updateBalance(transferTo);
                transactionRepo.save(transferTo);
                transactionRepo.save(transferFrom);

                Customer c=fromAccount.getOwnedAccountCustomer();
				List<Account> accounts = c.getOwnedAccounts();
				return accounts;

				//return fromAccount;

            } else {
                throw new IllegalArgumentException(String.format("Account %s has an insufficient balance to transfer the requested amount of: $%s",
                        fromAccount.getAccountId(), transferRequest.getTransferAmount()));
            }
        } else {
            throw new IllegalArgumentException(String.format("To (ID: %s) and From (ID: %s) Account IDs must be different",
                    fromAccount.getAccountId(), toAccount.getAccountId()));
        }
    }

	private void fundsCheck(TransactionCreationDTO transactionRequest, Account accountActual) {
    	if(accountActual.getAccountType().equals(BUSINESS_VISA) || accountActual.getAccountType().equals(PREMIUM_VISA)) {
    		if(!creditCardHasSufficientFunds(accountActual, transactionRequest)) {
				throw new IllegalArgumentException(String.format("The requested transaction exceeds the credit limit on the account %s",
						accountActual.getAccountId()));
			}
		} else if (!checkingHasSufficientFunds(accountActual, transactionRequest)) {
			throw new IllegalArgumentException(String.format("Account %s has an insufficient balance to debit the requested amount of: $%s",
					accountActual.getAccountId(), transactionRequest.getAmount()));
		}
	}

    private boolean checkingHasSufficientFunds(Account accountActual, TransactionCreationDTO transactionRequest) {
        return accountActual.getBalance().compareTo(transactionRequest.getAmount()) >= 0;
    }


    private boolean creditCardHasSufficientFunds(Account accountActual, TransactionCreationDTO transactionRequest) {
        if (accountActual.getAccountType().equals(BUSINESS_VISA)) {
            if (accountActual.getBalance().add(transactionRequest.getAmount().setScale(2)).compareTo(((BusinessVisa) accountActual).getCreditLimit().setScale(2)) != 1) {
                return true;
            }
        } else if (accountActual.getAccountType().equals(PREMIUM_VISA)) {
            if (accountActual.getBalance().add(transactionRequest.getAmount().setScale(2)).compareTo(((PremiumVisa) accountActual).getCreditLimit().setScale(2)) != 1) {
                return true;
            }
        }
        return false;
    }

	private void hasPayee(TransactionCreationDTO transactionRequest, Transaction transactionActual) {
		if (transactionRequest.getPayeeId() != null) {
			Payee payeeActual = payeeRepo.findById(transactionRequest.getPayeeId())
					.orElseThrow(() -> new IllegalArgumentException(String.format("Payee with ID: %s not found", transactionRequest.getPayeeId())));
			transactionActual.addPayee(payeeActual);
		}
	}

	private void creditCardAmountConversion(Account accountActual, Transaction transactionActual) {
		if (accountActual.getAccountType() == PREMIUM_VISA || accountActual.getAccountType() == BUSINESS_VISA) {
			transactionActual.setAmount(transactionActual.getAmount().multiply(BigDecimal.valueOf(-1.0)));
		}
	}

	private void requiresTransactionFee(Account accountActual, Transaction transactionActual) {
		if (accountActual.getAccountType().equals(CHECKING) && transactionActual.getTransactionType().equals(DEBIT)) {
			int remaining = ((Checking) accountActual).getMonthlyTransactionsRemaining();
			if (remaining > 0) {
				((Checking) accountActual).setMonthlyTransactionsRemaining(--remaining);
			} else {
				Transaction feeTransaction = Transaction.builder()
						.amount(((Checking) accountActual).getTransactionFee())
						.transactionType(TransactionTypeEnum.DEBIT)
						.transactionMemo(TransactionMemoEnum.TRANSACTION_FEE)
						.transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
						.build();
				accountActual.addTransaction(feeTransaction);
				accountActual.setBalance(accountActual.getBalance().subtract(feeTransaction.getAmount()));
				transactionRepo.save(feeTransaction);
			}
		}
	}

	private void applyCashBack(Account accountActual, Transaction transactionActual) {
		if (accountActual.getAccountType().equals(BUSINESS_VISA)) {
			BigDecimal cashbackAmount = transactionActual.getAmount().multiply(((BusinessVisa) accountActual).getCashBackRate());
			((BusinessVisa) accountActual).setCashBackAmount(((BusinessVisa) accountActual).getCashBackAmount().add(cashbackAmount));
		}

		if (accountActual.getAccountType().equals(PREMIUM_VISA)) {
			BigDecimal cashbackAmount = transactionActual.getAmount().multiply(((PremiumVisa) accountActual).getCashBackRate());
			((PremiumVisa) accountActual).setCashBackAmount(((PremiumVisa) accountActual).getCashBackAmount().add(cashbackAmount));
		}
	}
}

