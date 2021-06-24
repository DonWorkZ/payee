package com.fdmgroup.pilotbank.services;

import com.fdmgroup.pilotbank2.models.*;
import com.fdmgroup.pilotbank2.models.dto.TransactionCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.TransferRequestDTO;
import com.fdmgroup.pilotbank2.repo.AccountRepo;
import com.fdmgroup.pilotbank2.repo.PayeeRepo;
import com.fdmgroup.pilotbank2.repo.TransactionRepo;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import com.fdmgroup.pilotbank2.services.AccountServiceImpl;
import com.fdmgroup.pilotbank2.services.TransactionServiceImpl;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import com.fdmgroup.pilotbank2.type.TransactionMemoEnum;
import com.fdmgroup.pilotbank2.type.TransactionTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class TransactionServiceImplTest {
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepo mockTransactionRepo;

    @Mock
    private AccountRepo mockAccountRepo;

    @Mock
    private PayeeRepo mockPayeeRepo;

    private Address address;
    private Customer customer;
    private Checking checking;
    private Savings savings;
    private Transaction transaction;
    private TransactionCreationDTO transactionCreationDTO;
    private List<Transaction> transactions = new ArrayList<>();
    private Payee payee;

    @BeforeEach
    void init() {
        address = Address.builder()
                .streetNumber("123").streetName("Wall Street").suiteNumber(null)
                .city("New York").province("NY").postalCode("11111").build();

        customer = Customer.builder()
                .userId(999L).title("Mr.").firstName("Test").lastName("User")
                .username("Experiment999")
                .email("experiment999@email.com")
                .phoneNumber("626-626-6266")
                .sin("{bcrypt}$2a$10$mWkWlyaB89vtZPwRk0Gb1u3GZK08mDDc04tL/2Air0RFz.j0.2MR6")
                .password("{bcrypt}$2a$10$flvQjNBf1Fwd3ETm6Ttf8eVKPhvSzCGGj5xYBKOLTqRtwdmqwlGDu")
                .passwordExpires(LocalDateTime.of(2021,02,10,00,00,00,00)
                        .plusDays(90L))
                .role("CUSTOMER").isActive(true).lastFailedLoginDate(null).accountLockedFlag(false)
                .addressList(Arrays.asList(address))
                .accountExpires(null).industry("Travel").occupation("Driver").build();

        checking = Checking.builder()
                .accountId(1111L)
                .isMainAccount(true)
                .monthlyTransactionsRemaining(0)
                .accountType(AccountTypeEnum.valueOf("CHECKING"))
                .balance(BigDecimal.valueOf(100))
                .allTransactions(new ArrayList<>())
                .ownedAccountCustomer(customer)
                .openedByCustomer(customer)
                .build();

        transactionCreationDTO = TransactionCreationDTO.builder()
                                .amount(BigDecimal.valueOf(500))
                                .transactionType("CREDIT")
                                .transactionMemo("INITIAL_DEPOSIT")
                                .accountId(1111L)
                                .build();

        transaction = Transaction.builder()
                .account(checking)
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                .amount(transactionCreationDTO.getAmount())
                .transactionType(TransactionTypeEnum.valueOf(transactionCreationDTO.getTransactionType()))
                .transactionMemo(TransactionMemoEnum.valueOf(transactionCreationDTO.getTransactionMemo()))
                .build();

        transactions.add(transaction);

        transactionCreationDTO = TransactionCreationDTO.builder()
                .amount(BigDecimal.valueOf(100))
                .transactionType("DEBIT")
                .transactionMemo("GENERAL")
                .accountId(1111L)
                .build();

        transaction = Transaction.builder()
                .account(checking)
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                .amount(transactionCreationDTO.getAmount())
                .transactionType(TransactionTypeEnum.valueOf(transactionCreationDTO.getTransactionType()))
                .transactionMemo(TransactionMemoEnum.valueOf(transactionCreationDTO.getTransactionMemo()))
                .build();

        transactions.add(transaction);

        savings = Savings.builder()
                .accountId(2222L)
                .isMainAccount(false)
                .accountType(AccountTypeEnum.valueOf("SAVINGS"))
                .balance(BigDecimal.valueOf(0))
                .allTransactions(new ArrayList<>())
                .ownedAccountCustomer(customer)
                .openedByCustomer(customer)
                .build();

        payee = Payee.builder()
                .payeeId(6565L)
                .companyName("TestPayee")
                .nickname("TestNickName")
                .postalCode("74123")
                .accountNumber("258741")
                .customer(customer)
                .isActive(true)
                .transactions(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Test that findTransactionsByAccount returns a list of transactions")
    void testFindTransactionsByAccountReturnsTransactionList()
    {
        when(mockTransactionRepo.findAllByAccountOrderByTransactionDateDesc(checking)).thenReturn(transactions);
        List<Transaction> transactionList = transactionService.findTransactionsByAccount(checking);
        assertEquals(2, transactionList.size());
        assertEquals(BigDecimal.valueOf(500), transactionList.get(0).getAmount());
        assertEquals(TransactionTypeEnum.CREDIT, transactionList.get(0).getTransactionType());
        assertEquals(TransactionMemoEnum.INITIAL_DEPOSIT, transactionList.get(0).getTransactionMemo());
        assertEquals(BigDecimal.valueOf(100), transactionList.get(1).getAmount());
        assertEquals(TransactionTypeEnum.DEBIT, transactionList.get(1).getTransactionType());
        assertEquals(TransactionMemoEnum.GENERAL, transactionList.get(1).getTransactionMemo());
    }

    @Test
    @DisplayName("Test that createTransaction returns a new transaction")
    void testCreateTransactionReturnsNewTransaction() {
        TransactionCreationDTO newTransactionCreationDTO = TransactionCreationDTO.builder()
                                                        .amount(BigDecimal.valueOf(50))
                                                        .transactionType("DEBIT")
                                                        .transactionMemo("GENERAL")
                                                        .accountId(1111L)
                                                        .build();

        when(mockAccountRepo.findById(newTransactionCreationDTO.getAccountId())).thenReturn(Optional.of(checking));

        Transaction newTransaction = transactionService.createTransaction(newTransactionCreationDTO);

        assertEquals(BigDecimal.valueOf(50), newTransaction.getAmount());
        assertEquals(TransactionTypeEnum.DEBIT, newTransaction.getTransactionType());
        assertEquals(TransactionMemoEnum.GENERAL, newTransaction.getTransactionMemo());

        BusinessVisa businessVisa = BusinessVisa.builder()
                .accountId(3333L)
                .accountType(AccountTypeEnum.BUSINESS_VISA)
                .balance(BigDecimal.valueOf(0))
                .ownedAccountCustomer(customer)
                .isMainAccount(false)
                .creditLimit(BUSINESS_VISA_CREDIT_LIMIT)
                .build();

        newTransactionCreationDTO = TransactionCreationDTO.builder()
                .amount(BigDecimal.valueOf(30000))
                .transactionType("DEBIT")
                .transactionMemo("GENERAL")
                .accountId(3333L)
                .payeeId(6565L)
                .build();

        when(mockAccountRepo.findById(newTransactionCreationDTO.getAccountId())).thenReturn(Optional.of(businessVisa));
        when(mockPayeeRepo.findById(newTransactionCreationDTO.getPayeeId())).thenReturn(Optional.of(payee));

        newTransaction = transactionService.createTransaction(newTransactionCreationDTO);

        assertEquals(BigDecimal.valueOf(30000).setScale(2), newTransaction.getAmount());
        assertEquals(TransactionTypeEnum.DEBIT, newTransaction.getTransactionType());
        assertEquals(TransactionMemoEnum.GENERAL, newTransaction.getTransactionMemo());
    }

    @Test
    @DisplayName("Test that createTransaction returns a new transaction 2")
    void testCreateTransactionReturnsNewTransaction2() {
        TransactionCreationDTO newTransactionCreationDTO = TransactionCreationDTO.builder()
                .amount(BigDecimal.valueOf(50))
                .transactionType("DEBIT")
                .transactionMemo("GENERAL")
                .accountId(1111L)
                .build();

        when(mockAccountRepo.findById(newTransactionCreationDTO.getAccountId())).thenReturn(Optional.of(checking));

        Transaction newTransaction = transactionService.createTransaction(newTransactionCreationDTO);

        assertEquals(BigDecimal.valueOf(50), newTransaction.getAmount());
        assertEquals(TransactionTypeEnum.DEBIT, newTransaction.getTransactionType());
        assertEquals(TransactionMemoEnum.GENERAL, newTransaction.getTransactionMemo());

        PremiumVisa premiumVisa = PremiumVisa.builder()
                .accountId(3333L)
                .accountType(AccountTypeEnum.PREMIUM_VISA)
                .balance(BigDecimal.valueOf(0))
                .ownedAccountCustomer(customer)
                .isMainAccount(false)
                .creditLimit(PREMIUM_VISA_CREDIT_LIMIT)
                .build();

        newTransactionCreationDTO = TransactionCreationDTO.builder()
                .amount(BigDecimal.valueOf(3000))
                .transactionType("DEBIT")
                .transactionMemo("GENERAL")
                .accountId(3333L)
                .payeeId(6565L)
                .build();

        when(mockAccountRepo.findById(newTransactionCreationDTO.getAccountId())).thenReturn(Optional.of(premiumVisa));
        when(mockPayeeRepo.findById(newTransactionCreationDTO.getPayeeId())).thenReturn(Optional.of(payee));

        newTransaction = transactionService.createTransaction(newTransactionCreationDTO);

        assertEquals(BigDecimal.valueOf(3000).setScale(2), newTransaction.getAmount());
        assertEquals(TransactionTypeEnum.DEBIT, newTransaction.getTransactionType());
        assertEquals(TransactionMemoEnum.GENERAL, newTransaction.getTransactionMemo());
    }

    @Test
    @DisplayName("Test that providing incorrect account ID throws IllegalArgumentException")
    void testProvidingInvalidAccountIdThrowsIllegalArgumentException() {
        TransactionCreationDTO badTransactionCreationDTO = TransactionCreationDTO.builder()
                .amount(BigDecimal.valueOf(99))
                .transactionType("DEBIT")
                .transactionMemo("GENERAL")
                .accountId(9090L)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(badTransactionCreationDTO));

        String expectedMessage = String.format("Account with ID: %s not found", 9090L);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test that no debit transaction allowed with insufficient fund")
    void testNoDebitTransactionWithInsufficientFund() {
        TransactionCreationDTO newTransactionCreationDTO = TransactionCreationDTO.builder()
                                                    .amount(BigDecimal.valueOf(20000))
                                                    .transactionType("DEBIT")
                                                    .transactionMemo("GENERAL")
                                                    .accountId(1111L)
                                                    .build();

        when(mockAccountRepo.findById(newTransactionCreationDTO.getAccountId())).thenReturn(Optional.of(checking));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(newTransactionCreationDTO));

        String expectedMessage = String.format("Account %s has an insufficient balance to debit the requested amount of: $%s", 1111l, BigDecimal.valueOf(20000));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test that no debit transaction allowed if exceeding credit limit")
    void testNoDebitTransactionIfExceedingCreditLimit() {
        PremiumVisa premiumVisa = PremiumVisa.builder()
                .accountId(7777L)
                .accountType(AccountTypeEnum.PREMIUM_VISA)
                .balance(BigDecimal.valueOf(0))
                .ownedAccountCustomer(customer)
                .isMainAccount(false)
                .creditLimit(PREMIUM_VISA_CREDIT_LIMIT)
                .build();
        TransactionCreationDTO newTransactionCreationDTO = TransactionCreationDTO.builder()
                .amount(BigDecimal.valueOf(50001))
                .transactionType("DEBIT")
                .transactionMemo("GENERAL")
                .accountId(1111L)
                .build();

        when(mockAccountRepo.findById(newTransactionCreationDTO.getAccountId())).thenReturn(Optional.of(premiumVisa));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(newTransactionCreationDTO));

        String expectedMessage = String.format("The requested transaction exceeds the credit limit on the account %s", 7777L, BigDecimal.valueOf(20000));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
/*
    @Test
    @DisplayName("Test that transferFunds returns the from-transaction")
    void testTransferFundsReturnsFromTransaction() {
        TransferRequestDTO transferRequest = TransferRequestDTO.builder()
                .fromAccountId(checking.getAccountId())
                .toAccountId(savings.getAccountId())
                .transferAmount(BigDecimal.valueOf(50))
                .build();

        when(mockAccountRepo.findById(transferRequest.getFromAccountId())).thenReturn(Optional.of(checking));
        when(mockAccountRepo.findById(transferRequest.getToAccountId())).thenReturn(Optional.of(savings));

        Transaction transferFrom = transactionService.transferFunds(transferRequest);

        assertEquals(checking, transferFrom.getAccount());
        assertEquals(BigDecimal.valueOf(50), transferFrom.getAmount());
        assertEquals(TransactionTypeEnum.DEBIT, transferFrom.getTransactionType());
        assertEquals(TransactionMemoEnum.TRANSFER, transferFrom.getTransactionMemo());
    }
*/
    @Test
    @DisplayName("Test providing an incorrect from accountId throws an IllegalArgumentException")
    void testProvidingInvalidFromAccountIdThrowsIllegalArgumentException(){
        TransferRequestDTO badTransferRequest = TransferRequestDTO.builder()
                .fromAccountId(4040L)
                .toAccountId(savings.getAccountId())
                .transferAmount(BigDecimal.valueOf(50))
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> transactionService.transferFunds(badTransferRequest));
        String expectedMessage = String.format("Account with ID %s not found", 4040L);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test providing an incorrect to accountId throws an IllegalArgumentException")
    void testProvidingInvalidToAccountIdThrowsIllegalArgumentException(){
        TransferRequestDTO badTransferRequest = TransferRequestDTO.builder()
                                                .fromAccountId(checking.getAccountId())
                                                .toAccountId(5050L)
                                                .transferAmount(BigDecimal.valueOf(50))
                                                .build();

        when(mockAccountRepo.findById(badTransferRequest.getFromAccountId())).thenReturn(Optional.of(checking));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> transactionService.transferFunds(badTransferRequest));
        String expectedMessage = String.format("Account with ID %s not found", 5050L);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test transfer within the same account throws an IllegalArgumentException")
    void testTransferWithinSameAccountThrowsIllegalArgumentException() {
        TransferRequestDTO badTransferRequest = TransferRequestDTO.builder()
                .fromAccountId(checking.getAccountId())
                .toAccountId(checking.getAccountId())
                .transferAmount(BigDecimal.valueOf(50))
                .build();

        when(mockAccountRepo.findById(badTransferRequest.getFromAccountId())).thenReturn(Optional.of(checking));
        when(mockAccountRepo.findById(badTransferRequest.getToAccountId())).thenReturn(Optional.of(checking));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> transactionService.transferFunds(badTransferRequest));
        String expectedMessage = String.format("To (ID: %s) and From (ID: %s) Account IDs must be different", 1111L, 1111L);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test that no transfer allowed with insufficient fund")
    void testNoDebitTransferWithInsufficientFund() {
        TransferRequestDTO badTransferRequest = TransferRequestDTO.builder()
                .fromAccountId(checking.getAccountId())
                .toAccountId(savings.getAccountId())
                .transferAmount(BigDecimal.valueOf(5000))
                .build();

        when(mockAccountRepo.findById(badTransferRequest.getFromAccountId())).thenReturn(Optional.of(checking));
        when(mockAccountRepo.findById(badTransferRequest.getToAccountId())).thenReturn(Optional.of(savings));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> transactionService.transferFunds(badTransferRequest));

        String expectedMessage = String.format("Account %s has an insufficient balance to transfer the requested amount of: $%s", 1111l, BigDecimal.valueOf(5000));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
