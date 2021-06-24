package com.fdmgroup.pilotbank.controllers;

import com.fdmgroup.pilotbank2.controllers.TransactionController;
import com.fdmgroup.pilotbank2.models.*;
import com.fdmgroup.pilotbank2.models.dto.TransactionCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.TransferRequestDTO;
import com.fdmgroup.pilotbank2.repo.AccountRepo;
import com.fdmgroup.pilotbank2.repo.TransactionRepo;
import com.fdmgroup.pilotbank2.services.TransactionService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.ISO_DATE_TIME;
import static com.fdmgroup.pilotbank2.type.TransactionMemoEnum.*;
import static com.fdmgroup.pilotbank2.type.TransactionTypeEnum.CREDIT;
import static com.fdmgroup.pilotbank2.type.TransactionTypeEnum.DEBIT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService mockTransactionService;

    @Mock
    private AccountRepo mockAccountRepo;

    @Mock
    private TransactionRepo mockTransactionRepo;

    private Address address1;
    private Customer customer1;
    private Checking checking;
    private Savings savings;
    private Transaction transaction1, transaction2;
    private TransactionCreationDTO transactionCreationDTO = new TransactionCreationDTO();
    private List<Transaction> transactions = new ArrayList<>();
    private TransferRequestDTO transferRequestDTO = new TransferRequestDTO();

    @BeforeEach
    void init() {
        address1 = Address.builder()
                .streetNumber("123").streetName("Wall Street").suiteNumber(null)
                .city("New York").province("NY").postalCode("11111").build();

        customer1 = Customer.builder()
                .userId(999L).title("Mr.").firstName("Test").lastName("User")
                .username("Experiment999")
                .email("experiment999@email.com")
                .phoneNumber("626-626-6266")
                .sin("{bcrypt}$2a$10$mWkWlyaB89vtZPwRk0Gb1u3GZK08mDDc04tL/2Air0RFz.j0.2MR6")
                .password("{bcrypt}$2a$10$flvQjNBf1Fwd3ETm6Ttf8eVKPhvSzCGGj5xYBKOLTqRtwdmqwlGDu")
                .passwordExpires(LocalDateTime.of(2021, 02, 16, 00, 00, 00, 00)
                        .plusDays(90L))
                .role("CUSTOMER").isActive(true).lastFailedLoginDate(null).accountLockedFlag(false)
                .addressList(Arrays.asList(address1))
                .accountExpires(null).industry("Travel").occupation("Driver").build();

        checking = Checking.builder()
                .accountId(1111L)
                .accountType(AccountTypeEnum.CHECKING)
                .ownedAccountCustomer(customer1)
                .isMainAccount(true)
                .monthlyTransactionsRemaining(Checking.monthlyTransactionAmount)
                .build();

        savings = Savings.builder()
                .accountType(AccountTypeEnum.SAVINGS)
                .ownedAccountCustomer(customer1)
                .isMainAccount(false)
                .build();

        transactionCreationDTO = TransactionCreationDTO.builder()
                                .amount(BigDecimal.valueOf(200L))
                                .transactionType("CREDIT")
                                .transactionMemo("GENERAL")
                                .accountId(checking.getAccountId())
                                .build();

        transaction1 = Transaction.builder()
                        .amount(transactionCreationDTO.getAmount())
                        .transactionType(TransactionTypeEnum.valueOf(transactionCreationDTO.getTransactionType()))
                        .transactionMemo(TransactionMemoEnum.valueOf(transactionCreationDTO.getTransactionMemo()))
                        .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                        .build();

        transactionCreationDTO = TransactionCreationDTO.builder()
                .amount(BigDecimal.valueOf(150L))
                .transactionType("DEBIT")
                .transactionMemo("GENERAL")
                .accountId(checking.getAccountId())
                .build();

        transaction2 = Transaction.builder()
                .amount(transactionCreationDTO.getAmount())
                .transactionType(TransactionTypeEnum.valueOf(transactionCreationDTO.getTransactionType()))
                .transactionMemo(TransactionMemoEnum.valueOf(transactionCreationDTO.getTransactionMemo()))
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                .build();

        transactions.add(transaction1);
        transactions.add(transaction2);
        checking.setAllTransactions(transactions);

        transferRequestDTO = TransferRequestDTO.builder()
                .fromAccountId(checking.getAccountId())
                .toAccountId(savings.getAccountId())
                .transferAmount(BigDecimal.valueOf(30L))
                .build();
    }

    @Test
    @DisplayName("test that getTransactions returns a transaction list")
    void test_getTransactions_returns_account_list() {
        when(mockAccountRepo.findById(checking.getAccountId())).thenReturn(Optional.of(checking));

        ResponseEntity<List<Transaction>> transactionList = transactionController.getTransactions(checking.getAccountId());
        verify(mockAccountRepo, times(1)).findById(checking.getAccountId());
        assertEquals(2, transactionList.getBody().size());
        assertEquals(transaction1, transactionList.getBody().get(0));
        assertEquals(transaction2, transactionList.getBody().get(1));
        assertEquals(HttpStatus.OK, transactionList.getStatusCode());
    }

    @Test
    @DisplayName("test that getTransactions throws an exception")
    void test_getTransactions_throws_exception(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> transactionController.getTransactions(42L));

        String expectedMessage = String.format("Account with ID: %s not found", 42L);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test that createTransaction returns a new transaction")
    void test_createTransaction_returns_newTransaction() {
        transactionCreationDTO = TransactionCreationDTO.builder()
                .amount(BigDecimal.valueOf(5000L))
                .transactionType("CREDIT")
                .transactionMemo("GENERAL")
                .accountId(checking.getAccountId())
                .build();

        Transaction transaction3 = Transaction.builder()
                .amount(transactionCreationDTO.getAmount())
                .transactionType(TransactionTypeEnum.valueOf(transactionCreationDTO.getTransactionType()))
                .transactionMemo(TransactionMemoEnum.valueOf(transactionCreationDTO.getTransactionMemo()))
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                .build();

        when(mockTransactionService.createTransaction(transactionCreationDTO)).thenReturn(transaction3);
        ResponseEntity<?> transaction = transactionController.createTransaction(transactionCreationDTO);
        verify(mockTransactionService, times(1)).createTransaction(transactionCreationDTO);
        assertEquals(transaction3, transaction.getBody());
        assertEquals(HttpStatus.CREATED, transaction.getStatusCode());
    }

    @Test
    @DisplayName("Test that createAccount throws exception")
    void test_createAccount_throws_Exception() {
        when(mockTransactionService.createTransaction(transactionCreationDTO))
                .thenThrow(new IllegalArgumentException("IllegalArgumentException") {});
        ResponseEntity<?> transaction = transactionController.createTransaction(transactionCreationDTO);

        String expectedMessage = String.format("Transaction could not be created for Account ID: %s. Error: %s.",
                transactionCreationDTO.getAccountId(),
                "com.fdmgroup.pilotbank.controllers.TransactionControllerTest$1: IllegalArgumentException");
        assertEquals(expectedMessage, transaction.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, transaction.getStatusCode());
    }

    /*
    @Test
    @DisplayName("Test that transferFunds returns a new transaction")
    void test_transferFunds_returns_newTransaction() {
        Transaction transferFrom = Transaction.builder()
                                    .account(checking)
                                    .transactionType(DEBIT)
                                    .transactionMemo(TRANSFER)
                                    .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                                    .amount(transferRequestDTO.getTransferAmount())
                                    .build();

        when(mockTransactionService.transferFunds(transferRequestDTO)).thenReturn(transferFrom);
        ResponseEntity<?> transaction = transactionController.transferFunds(transferRequestDTO);
        verify(mockTransactionService, times(1)).transferFunds(transferRequestDTO);
        assertEquals(transferFrom, transaction.getBody());
        assertEquals(HttpStatus.OK, transaction.getStatusCode());
    }
    */
    @Test
    @DisplayName("Test that transferFunds throws exception")
    void test_transferFunds_throws_Exception() {
        when(mockTransactionService.transferFunds(transferRequestDTO))
                .thenThrow(new IllegalArgumentException("IllegalArgumentException") {});
        ResponseEntity<?> transaction = transactionController.transferFunds(transferRequestDTO);

        String expectedMessage = String.format("Funds Transfer failed: %s",
                "com.fdmgroup.pilotbank.controllers.TransactionControllerTest$2: IllegalArgumentException");
        assertEquals(expectedMessage, transaction.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, transaction.getStatusCode());
    }

    @Test
    @DisplayName("Test that getStatement returns a transaction list of specified month")
    void test_getStatement_returns_transactionList_of_specifiedMonth() {
        Transaction transaction1 = Transaction.builder()
                .account(checking)
                .transactionType(CREDIT)
                .transactionMemo(GENERAL)
                .transactionDate(LocalDateTime.parse("2020-02-01T00:00:00", ISO_DATE_TIME))
                .amount(BigDecimal.valueOf(200))
                .build();

        Transaction transaction2 = Transaction.builder()
                .account(checking)
                .transactionType(CREDIT)
                .transactionMemo(GENERAL)
                .transactionDate(LocalDateTime.parse("2020-02-15T12:30:30", ISO_DATE_TIME))
                .amount(BigDecimal.valueOf(500))
                .build();

        Transaction transaction3 = Transaction.builder()
                .account(checking)
                .transactionType(DEBIT)
                .transactionMemo(GENERAL)
                .transactionDate(LocalDateTime.parse("2020-02-28T23:59:59", ISO_DATE_TIME))
                .amount(BigDecimal.valueOf(300))
                .build();

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction1);
        transactionList.add(transaction2);
        transactionList.add(transaction3);
        checking.setAllTransactions(transactionList);

        when(mockAccountRepo.findById(checking.getAccountId())).thenReturn(Optional.ofNullable(checking));

        when(mockTransactionRepo.findMonthlyStatementByAccount(
                checking,
                LocalDateTime.parse("2021-02-01T00:00", ISO_DATE_TIME),
                LocalDateTime.parse("2021-02-28T23:59:59.999999999", ISO_DATE_TIME)
        )).thenReturn(transactionList);

        List<Transaction> transactions = transactionController.getStatement(checking.getAccountId(), "2", "2021");
        verify(mockAccountRepo, times(1)).findById(checking.getAccountId());
        assertEquals(3, transactions.size());
        assertEquals(CREDIT, transactions.get(0).getTransactionType());
        assertEquals(CREDIT, transactions.get(1).getTransactionType());
        assertEquals(DEBIT, transactions.get(2).getTransactionType());
        assertEquals(BigDecimal.valueOf(200), transactions.get(0).getAmount());
        assertEquals(BigDecimal.valueOf(500), transactions.get(1).getAmount());
        assertEquals(BigDecimal.valueOf(300), transactions.get(2).getAmount());
    }

    @Test
    @DisplayName("Test that getStatement throws IllegalArgumentException")
    void test_getStatement_throws_IllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                ()-> transactionController.getStatement(9999L, "2", "2021"));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Account with ID: 9999 not found"));
    }

    @Test
    @DisplayName("Test that getStatement throws NumberFormatException with an invalid month value")
    void test_getStatement_throws_NumberFormatException_with_invalid_month_value() {
        when(mockAccountRepo.findById(checking.getAccountId())).thenReturn(Optional.ofNullable(checking));
        Exception exception = assertThrows(NumberFormatException.class,
                ()-> transactionController.getStatement(checking.getAccountId(), "0", "2021"));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Invalid month value. The value must be between '1' and '12' (both inclusive)."));
    }

    @Test
    @DisplayName("Test that getStatement throws NumberFormatException with an invalid year value")
    void test_getStatement_throws_NumberFormatException_with_invalid_year_value() {
        when(mockAccountRepo.findById(checking.getAccountId())).thenReturn(Optional.ofNullable(checking));
        Exception exception = assertThrows(NumberFormatException.class,
                ()-> transactionController.getStatement(checking.getAccountId(), "12", "-1"));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Invalid year value. The value must be greater than or equal to '0'."));
    }

    @Test
    @DisplayName("Test that getStatement throws IllegalArgumentException when the statement is null")
    void test_getStatement_throws_IllegalArgumentException_when_statement_is_null() {
        when(mockAccountRepo.findById(checking.getAccountId())).thenReturn(Optional.ofNullable(checking));
        when(mockTransactionRepo.findMonthlyStatementByAccount(
                checking,
                LocalDateTime.parse("2022-01-01T00:00", ISO_DATE_TIME),
                LocalDateTime.parse("2022-01-31T23:59:59.999999999", ISO_DATE_TIME)
        )).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class,
                ()-> transactionController.getStatement(checking.getAccountId(), "1", "2022"));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Unable to find a transaction for the account with the specified month and year."));
    }

    @Test
    @DisplayName("Test that getStatementList returns all the statements of a specified account")
    void test_getStatementList_returns_allStatements_of_specifiedAccount() {
        Transaction transaction1 = Transaction.builder()
                .account(checking)
                .transactionType(CREDIT)
                .transactionMemo(INTERNATIONAL_CURRENCY)
                .transactionDate(LocalDateTime.parse("2020-12-01T00:00:00", ISO_DATE_TIME))
                .amount(BigDecimal.valueOf(200))
                .build();

        Transaction transaction2 = Transaction.builder()
                .account(checking)
                .transactionType(CREDIT)
                .transactionMemo(GENERAL)
                .transactionDate(LocalDateTime.parse("2021-01-16T12:30:30", ISO_DATE_TIME))
                .amount(BigDecimal.valueOf(500))
                .build();

        Transaction transaction3 = Transaction.builder()
                .account(checking)
                .transactionType(DEBIT)
                .transactionMemo(GENERAL)
                .transactionDate(LocalDateTime.parse("2021-02-25T23:59:59", ISO_DATE_TIME))
                .amount(BigDecimal.valueOf(300))
                .build();

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction1);
        transactionList.add(transaction2);
        transactionList.add(transaction3);
        checking.setAllTransactions(transactionList);
        checking.setAccountCreationDate(LocalDateTime.parse("2020-12-01T00:00:00", ISO_DATE_TIME));

        when(mockAccountRepo.findById(checking.getAccountId())).thenReturn(Optional.ofNullable(checking));

        List<ArrayList<Transaction>> statements = transactionController.getStatementList(checking.getAccountId());

        long monthsBetween = ChronoUnit.MONTHS.between(
                YearMonth.from(LocalDate.parse("2020-12-01")),
                YearMonth.from(LocalDate.now())
        );

        verify(mockAccountRepo, times(1)).findById(checking.getAccountId());
        assertEquals((int) monthsBetween, statements.size());
        assertEquals(1, statements.get((int) monthsBetween-3).size());
        assertEquals(1, statements.get((int) monthsBetween-2).size());
        assertEquals(1, statements.get((int) monthsBetween-1).size());
        assertEquals(BigDecimal.valueOf(300), statements.get((int) monthsBetween-3).get(0).getAmount());
        assertEquals(BigDecimal.valueOf(500), statements.get((int) monthsBetween-2).get(0).getAmount());
        assertEquals(BigDecimal.valueOf(200), statements.get((int) monthsBetween-1).get(0).getAmount());
        assertEquals(GENERAL, statements.get((int) monthsBetween-3).get(0).getTransactionMemo());
        assertEquals(GENERAL, statements.get((int) monthsBetween-2).get(0).getTransactionMemo());
        assertEquals(INTERNATIONAL_CURRENCY, statements.get((int) monthsBetween-1).get(0).getTransactionMemo());
    }

    @Test
    @DisplayName("Test that getStatementList returns all the statements of a specified account2")
    void test_getStatementList_returns_allStatements_of_specifiedAccount2() {
        Transaction transaction1 = Transaction.builder()
                .account(checking)
                .transactionType(CREDIT)
                .transactionMemo(INTERNATIONAL_CURRENCY)
                .transactionDate(LocalDateTime.parse("2020-09-01T00:00:00", ISO_DATE_TIME))
                .amount(BigDecimal.valueOf(200))
                .build();

        Transaction transaction2 = Transaction.builder()
                .account(checking)
                .transactionType(CREDIT)
                .transactionMemo(GENERAL)
                .transactionDate(LocalDateTime.parse("2020-11-16T12:30:30", ISO_DATE_TIME))
                .amount(BigDecimal.valueOf(500))
                .build();

        Transaction transaction3 = Transaction.builder()
                .account(checking)
                .transactionType(DEBIT)
                .transactionMemo(GENERAL)
                .transactionDate(LocalDateTime.parse("2020-11-25T23:59:59", ISO_DATE_TIME))
                .amount(BigDecimal.valueOf(300))
                .build();

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction1);
        transactionList.add(transaction2);
        transactionList.add(transaction3);
        checking.setAllTransactions(transactionList);
        checking.setAccountCreationDate(LocalDateTime.parse("2020-09-01T00:00:00", ISO_DATE_TIME));

        when(mockAccountRepo.findById(checking.getAccountId())).thenReturn(Optional.ofNullable(checking));

        List<ArrayList<Transaction>> statements = transactionController.getStatementList(checking.getAccountId());

        long monthsBetween = ChronoUnit.MONTHS.between(
                YearMonth.from(LocalDate.parse("2020-09-01")),
                YearMonth.from(LocalDate.now())
        );

        verify(mockAccountRepo, times(1)).findById(checking.getAccountId());
        assertEquals((int) monthsBetween, statements.size());
        assertEquals(1, statements.get((int) monthsBetween-1).size());
        assertEquals(2, statements.get((int) monthsBetween-3).size());
        assertEquals(BigDecimal.valueOf(200), statements.get((int) monthsBetween-1).get(0).getAmount());
        assertEquals(BigDecimal.valueOf(500), statements.get((int) monthsBetween-3).get(0).getAmount());
        assertEquals(BigDecimal.valueOf(300), statements.get((int) monthsBetween-3).get(1).getAmount());
        assertEquals(INTERNATIONAL_CURRENCY, statements.get((int) monthsBetween-1).get(0).getTransactionMemo());
        assertEquals(GENERAL, statements.get((int) monthsBetween-3).get(0).getTransactionMemo());
        assertEquals(GENERAL, statements.get((int) monthsBetween-3).get(1).getTransactionMemo());
    }

    @Test
    @DisplayName("Test that getStatementList throws IllegalArgumentException")
    void test_getStatementList_throws_IllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                ()-> transactionController.getStatementList(9999L));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Account with ID: 9999 not found"));
    }
}
