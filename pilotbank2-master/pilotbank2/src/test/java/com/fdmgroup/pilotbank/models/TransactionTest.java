package com.fdmgroup.pilotbank.models;

import com.fdmgroup.pilotbank2.models.*;
import com.fdmgroup.pilotbank2.models.dto.TransactionCreationDTO;
import com.fdmgroup.pilotbank2.services.TransactionServiceImpl;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import com.fdmgroup.pilotbank2.type.TransactionMemoEnum;
import com.fdmgroup.pilotbank2.type.TransactionTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.*;
import static com.fdmgroup.pilotbank2.type.TransactionMemoEnum.GENERAL;
import static com.fdmgroup.pilotbank2.type.TransactionTypeEnum.CREDIT;
import static com.fdmgroup.pilotbank2.type.TransactionTypeEnum.DEBIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class TransactionTest {

    @Spy
    private TransactionServiceImpl mockTransactionService;

    private TransactionCreationDTO transactionCreationRequest;
    private Checking checking;
    private BusinessVisa businessVisa;
    private PremiumVisa premiumVisa;
    private Customer customer;
    private Transaction transaction;
    private Payee payee;

    @BeforeEach
    void init() {
        customer = Customer.builder()
                .userId(100L)
                .username("UnitTest")
                .title("Mr.")
                .firstName("Unit")
                .lastName("Test")
                .email("unit.test@email.com")
                .phoneNumber("111-111-1111")
                .password("1A2b3c4!")
                .role("CUSTOMER")
                .sin("111-111-999")
                .industry("Energy")
                .occupation("Sales")
                .build();

        checking = Checking.builder()
                .accountId(1000L)
                .accountType(AccountTypeEnum.CHECKING)
                .ownedAccountCustomer(customer)
                .balance(BigDecimal.valueOf(500L))
                .isMainAccount(true)
                .build();

        businessVisa = BusinessVisa.builder()
                .accountType(AccountTypeEnum.BUSINESS_VISA)
                .ownedAccountCustomer(customer)
                .isMainAccount(false)
                .balance(BigDecimal.valueOf(500L))
                .creditLimit(BUSINESS_VISA_CREDIT_LIMIT)
                .build();

        premiumVisa = PremiumVisa.builder()
                .accountType(AccountTypeEnum.PREMIUM_VISA)
                .ownedAccountCustomer(customer)
                .isMainAccount(false)
                .balance(BigDecimal.valueOf(500L))
                .creditLimit(PREMIUM_VISA_CREDIT_LIMIT)
                .build();

        payee = Payee.builder()
                .payeeId(7890L)
                .companyName("Mock Payee")
                .build();

        transactionCreationRequest = new TransactionCreationDTO(
                BigDecimal.valueOf(100L), "CREDIT", "GENERAL", 1000L, null);

        transaction = Transaction.builder()
                .transactionId(3210)
                .amount(transactionCreationRequest.getAmount())
                .transactionType(TransactionTypeEnum.valueOf(transactionCreationRequest.getTransactionType()))
                .transactionMemo(TransactionMemoEnum.valueOf(transactionCreationRequest.getTransactionMemo()))
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                .payee(payee)
                .build();
    }

    @Test
    @DisplayName("Test if a credit transaction correctly increases an account balance")
    void test_if_credit_transaction_correctly_increases_account_balance() {
        transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(100L))
                .transactionType(CREDIT)
                .transactionMemo(GENERAL)
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                .build();
        checking.updateBalance(transaction);
        assertEquals(BigDecimal.valueOf(600L), checking.getBalance());
    }

    @Test
    @DisplayName("Test if a debit transaction correctly decreases an account balance")
    void test_if_debit_transaction_correctly_decreases_account_balance() {
        transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(100L))
                .transactionType(DEBIT)
                .transactionMemo(GENERAL)
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                .build();
        checking.updateBalance(transaction);
        assertEquals(BigDecimal.valueOf(400L), checking.getBalance());
    }

    @Test
    @DisplayName("Test if a transaction has a correct attributes")
    void test_if_transaction_has_correct_attributes() {
        doReturn(transaction).when(mockTransactionService).createTransaction(transactionCreationRequest);
        Transaction transaction1 = mockTransactionService.createTransaction(transactionCreationRequest);

        assertEquals(3210, transaction1.getTransactionId());
        assertEquals(BigDecimal.valueOf(100L), transaction1.getAmount());
        assertEquals(CREDIT, transaction1.getTransactionType());
        assertEquals(GENERAL, transaction1.getTransactionMemo());
        assertEquals(transaction.getTransactionDate(), transaction1.getTransactionDate());
        assertEquals(payee, transaction1.getPayee());
    }

    @Test
    @DisplayName("Test if the no arg constructor creates a transaction with default values")
    void test_if_no_arg_constructor_creates_transaction_with_default_values() {
        Transaction defaultTransaction = new Transaction();
        assertEquals(0, defaultTransaction.getTransactionId());
        assertEquals(null, defaultTransaction.getAmount());
        assertEquals(null, defaultTransaction.getTransactionType());
        assertEquals(null, defaultTransaction.getTransactionMemo());
        assertEquals(null, defaultTransaction.getTransactionDate());
        assertEquals(null, defaultTransaction.getAccount());
        assertEquals(null, defaultTransaction.getPayee());
    }

    @Test
    @DisplayName("Test if the all arg constructor creates a transaction with specified values")
    void test_if_all_arg_constructor_creates_transaction_with_specified_values() {
        Payee newPayee = new Payee();

        Transaction allArgTransaction = new Transaction(1111, BigDecimal.valueOf(99),
                CREDIT, GENERAL, null, premiumVisa, newPayee);
        assertEquals(1111, allArgTransaction.getTransactionId());
        assertEquals(BigDecimal.valueOf(99), allArgTransaction.getAmount());
        assertEquals(CREDIT, allArgTransaction.getTransactionType());
        assertEquals(GENERAL, allArgTransaction.getTransactionMemo());
        assertEquals(null, allArgTransaction.getTransactionDate());
        assertEquals(premiumVisa, allArgTransaction.getAccount());
        assertEquals(newPayee, allArgTransaction.getPayee());
    }

    @Test
    @DisplayName("Test if toString() returns the expected string")
    void test_if_toString_returns_expected_string() {
        doReturn(transaction).when(mockTransactionService).createTransaction(transactionCreationRequest);
        Transaction transaction1 = mockTransactionService.createTransaction(transactionCreationRequest);
        String expectedStr = "Transaction [transactionId=" + 3210 + ", amount=" + BigDecimal.valueOf(100L)
                + ", transactionType=" + CREDIT.getTransactionTypeName() + ", transactionMemo=" + GENERAL.getTransactionMemoName()
                + ", transactionDate=" + transaction.getTransactionDate() + "]";
        assertEquals(expectedStr, transaction1.toString());
    }

    @Test
    @DisplayName("Test if removePayee() removes a payee from the transaction")
    void test_if_removePayee_removes_payee_from_transaction() {
        transaction.removePayee(payee);
        assertNull(transaction.getPayee());
    }
}
