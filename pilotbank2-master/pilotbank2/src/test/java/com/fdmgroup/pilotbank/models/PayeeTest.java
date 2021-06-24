package com.fdmgroup.pilotbank.models;

import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.Payee;
import com.fdmgroup.pilotbank2.models.Transaction;
import com.fdmgroup.pilotbank2.models.dto.PayeeCreationDTO;
import com.fdmgroup.pilotbank2.services.PayeeServiceImpl;
import com.fdmgroup.pilotbank2.services.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.ISO_DATE_TIME;
import static com.fdmgroup.pilotbank2.type.TransactionMemoEnum.GENERAL;
import static com.fdmgroup.pilotbank2.type.TransactionTypeEnum.CREDIT;
import static com.fdmgroup.pilotbank2.type.TransactionTypeEnum.DEBIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class PayeeTest {

    @Spy
    private PayeeServiceImpl mockPayeeService;

    private Customer customer;
    private PayeeCreationDTO payeeCreationDTO;
    private Payee payee;
    private Transaction transaction;

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

        payeeCreationDTO = new PayeeCreationDTO(
                            "Test Payee",
                            "12345",
                            "9876543210",
                            "TP",
                            100L
                            );

        payee = Payee.builder()
                .payeeId(1234L)
                .customer(customer)
                .companyName(payeeCreationDTO.getCompanyName())
                .postalCode(payeeCreationDTO.getPostalCode())
                .accountNumber(payeeCreationDTO.getAccountNumber())
                .nickname(payeeCreationDTO.getNickname())
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Test if the payee has the associated customer")
    void test_if_payee_has_associated_customer() {
        doReturn(payee).when(mockPayeeService).createPayee(payeeCreationDTO);
        mockPayeeService.createPayee(payeeCreationDTO);
        assertEquals(customer, payee.getCustomer());
    }

    @Test
    @DisplayName("Test if the payee is active at the time of creation")
    void test_if_payee_is_default_to_active() {
        doReturn(payee).when(mockPayeeService).createPayee(payeeCreationDTO);
        mockPayeeService.createPayee(payeeCreationDTO);
        assertEquals(true, payee.isActive());
    }

    @Test
    @DisplayName("Test if the payee can have a list of transactions")
    void test_if_payee_has_transactions() {
        List<Transaction> transactionsList = new ArrayList<>();
        transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(100L))
                .transactionType(DEBIT)
                .transactionMemo(GENERAL)
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                .build();
        transactionsList.add(transaction);
        transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(200L))
                .transactionType(CREDIT)
                .transactionMemo(GENERAL)
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                .build();
        transactionsList.add(transaction);
        transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(300L))
                .transactionType(CREDIT)
                .transactionMemo(GENERAL)
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                .build();
        transactionsList.add(transaction);

        payee.setTransactions(transactionsList);

        doReturn(payee).when(mockPayeeService).createPayee(payeeCreationDTO);
        mockPayeeService.createPayee(payeeCreationDTO);
        assertEquals(3, payee.getTransactions().size());
        assertEquals(BigDecimal.valueOf(100L), payee.getTransactions().get(0).getAmount());
        assertEquals(DEBIT, payee.getTransactions().get(0).getTransactionType());
        assertEquals(BigDecimal.valueOf(200L), payee.getTransactions().get(1).getAmount());
        assertEquals(CREDIT, payee.getTransactions().get(1).getTransactionType());
        assertEquals(BigDecimal.valueOf(300L), payee.getTransactions().get(2).getAmount());
        assertEquals(CREDIT, payee.getTransactions().get(2).getTransactionType());
    }

    @Test
    @DisplayName("Test if toString() returns the expected string")
    void test_if_toString_returns_expected_string() {
        doReturn(payee).when(mockPayeeService).createPayee(payeeCreationDTO);
        Payee payee1 = mockPayeeService.createPayee(payeeCreationDTO);
        String expectedStr = "Payee: [payeeId=" + 1234L + "companyName=" + "Test Payee"
                + "postalCode=" + "12345" + "accountNumber=" + "9876543210"
                + "nickname=" + "TP" + "isActive=" + true + "]";
        assertEquals(expectedStr, payee1.toString());
    }
}
