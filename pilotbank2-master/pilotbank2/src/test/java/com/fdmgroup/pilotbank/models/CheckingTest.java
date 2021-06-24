package com.fdmgroup.pilotbank.models;

import com.fdmgroup.pilotbank2.models.Checking;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.dto.AccountCreationDTO;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class CheckingTest {

    private Customer customer;
    private AccountCreationDTO accountCreationRequest;
    private Checking checking;

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

        accountCreationRequest = AccountCreationDTO.builder()
                .openedByCustomerId(100L)
                .accountType(AccountTypeEnum.CHECKING.toString())
                .balance(BigDecimal.valueOf(100))
                .isMainAccount(true)
                .build();

        checking = Checking.builder()
                .accountId(1000L)
                .accountType(AccountTypeEnum.valueOf(accountCreationRequest.getAccountType()))
                .ownedAccountCustomer(customer)
                .balance(accountCreationRequest.getBalance())
                .monthlyTransactionsRemaining(CHECKING_MONTHLY_TRANSACTION_LIMIT)
                .hasMonthlyServiceFee(false)
                .isMainAccount(accountCreationRequest.getIsMainAccount())
                .build();
    }

    @Test
    @DisplayName("Test if the checking has the expected attributes")
    void test_if_checking_has_expected_attributes() {
        assertEquals(CHECKING_MONTHLY_TRANSACTION_LIMIT, Checking.monthlyTransactionAmount);
        assertEquals(CHECKING_MONTHLY_TRANSACTION_LIMIT, checking.getMonthlyTransactionsRemaining());
        assertEquals(CHECKING_TRANSACTION_FEE, checking.getTransactionFee());
        assertEquals(false, checking.getHasMonthlyServiceFee());
        assertEquals(CHECKING_MONTHLY_FEE, checking.getMonthlyServiceFee());
        assertEquals(CHECKING_MINIMUM_BALANCE, checking.getMonthlyMinimumBalance());
        assertEquals(CHECKING_E_TRANSFER_FEE, checking.getETransferFee());
    }

    @Test
    @DisplayName("Test if the no arg constructor creates a checking with default values")
    void test_if_no_arg_constructor_creates_checking_with_default_values() {
        Checking defaultChecking = new Checking();
        assertEquals(0, defaultChecking.getMonthlyTransactionsRemaining());
        assertEquals(CHECKING_TRANSACTION_FEE, defaultChecking.getTransactionFee());
        assertEquals(false, defaultChecking.getHasMonthlyServiceFee());
        assertEquals(CHECKING_MONTHLY_FEE, defaultChecking.getMonthlyServiceFee());
        assertEquals(CHECKING_MINIMUM_BALANCE, defaultChecking.getMonthlyMinimumBalance());
        assertEquals(CHECKING_E_TRANSFER_FEE, defaultChecking.getETransferFee());
    }

    @Test
    @DisplayName("Test if the all arg constructor creates a checking with specified values")
    void test_if_all_arg_constructor_creates_checking_with_specified_values() {
        Checking allArgChecking = new Checking(10, BigDecimal.valueOf(20), true,
                BigDecimal.valueOf(30), BigDecimal.valueOf(40), BigDecimal.valueOf(50));
        assertEquals(10, allArgChecking.getMonthlyTransactionsRemaining());
        assertEquals(BigDecimal.valueOf(20), allArgChecking.getTransactionFee());
        assertEquals(true, allArgChecking.getHasMonthlyServiceFee());
        assertEquals(BigDecimal.valueOf(30), allArgChecking.getMonthlyServiceFee());
        assertEquals(BigDecimal.valueOf(40), allArgChecking.getMonthlyMinimumBalance());
        assertEquals(BigDecimal.valueOf(50), allArgChecking.getETransferFee());
    }

    @Test
    @DisplayName("Test if the setter updates an attribute value")
    void test_if_setter_updates_attribute_value() {
        assertEquals(false, checking.getHasMonthlyServiceFee());
        checking.setHasMonthlyServiceFee(true);
        assertTrue(checking.getHasMonthlyServiceFee());
    }

    @Test
    @DisplayName("Test if toString() returns the expected string")
    void test_if_toString_returns_expected_string() {
        String expectedStr = "Account [accountId=" + 1000L + ", balance=" + BigDecimal.valueOf(100)
                + ", accountCreationDate=" + null + ", accountType=" + AccountTypeEnum.CHECKING.toString() + "]"
                + " Checking [" + "monthlyTransactionsRemaining=" + CHECKING_MONTHLY_TRANSACTION_LIMIT
                + ", transactionFee=" + CHECKING_TRANSACTION_FEE +
                ", hasMonthlyServiceFee=" + false + ", monthlyServiceFee=" + CHECKING_MONTHLY_FEE +
                ", monthlyMinimumBalance=" + CHECKING_MINIMUM_BALANCE +
                ", eTransferFee=" + CHECKING_E_TRANSFER_FEE + "]";
        assertEquals(expectedStr, checking.toString());
    }
}
