package com.fdmgroup.pilotbank.models;

import com.fdmgroup.pilotbank2.models.Checking;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.Savings;
import com.fdmgroup.pilotbank2.models.dto.AccountCreationDTO;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.*;
import static com.fdmgroup.pilotbank2.common.PilotBankConstants.CHECKING_E_TRANSFER_FEE;
import static com.fdmgroup.pilotbank2.type.TransactionMemoEnum.MINIMUM_BALANCE_CHARGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class SavingsTest {

    private Customer customer;
    private AccountCreationDTO accountCreationRequest;
    private Savings savings;

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
                .accountType(AccountTypeEnum.SAVINGS.toString())
                .balance(BigDecimal.valueOf(100))
                .isMainAccount(true)
                .build();

        savings = Savings.builder()
                .accountId(1000L)
                .accountType(AccountTypeEnum.valueOf(accountCreationRequest.getAccountType()))
                .ownedAccountCustomer(customer)
                .balance(accountCreationRequest.getBalance())
                .minBalance(SAVINGS_MINIMUM_BALANCE)
                .minBalanceCharge(MINIMUM_BALANCE_CHARGE.getTransactionFee())
                .interestRate(SAVINGS_INTEREST_RATE)
                .build();
    }

    @Test
    @DisplayName("Test if the savings has the expected attributes")
    void test_if_savings_has_expected_attributes() {
        assertEquals(SAVINGS_MINIMUM_BALANCE, savings.getMinBalance());
        assertEquals(MINIMUM_BALANCE_CHARGE.getTransactionFee(), savings.getMinBalanceCharge());
        assertEquals(SAVINGS_INTEREST_RATE, savings.getInterestRate());
    }

    @Test
    @DisplayName("Test if the no arg constructor creates a savings with default values")
    void test_if_no_arg_constructor_creates_savings_with_specified_values() {
        Savings defaultSavings = new Savings();
        assertEquals(BigDecimal.valueOf(0).setScale(1), defaultSavings.getMinBalance());
        assertEquals(BigDecimal.valueOf(0).setScale(1), defaultSavings.getMinBalanceCharge());
        assertEquals(SAVINGS_INTEREST_RATE, defaultSavings.getInterestRate());
    }

    @Test
    @DisplayName("Test if the all arg constructor creates a savings with specified values")
    void test_if_all_arg_constructor_creates_savings_with_default_values() {
        Savings allArgSavings = new Savings(BigDecimal.valueOf(10),
                BigDecimal.valueOf(20), BigDecimal.valueOf(30));
        assertEquals(BigDecimal.valueOf(10), allArgSavings.getMinBalance());
        assertEquals(BigDecimal.valueOf(20), allArgSavings.getMinBalanceCharge());
        assertEquals(BigDecimal.valueOf(30), allArgSavings.getInterestRate());
    }

    @Test
    @DisplayName("Test if the setter updates an attribute value")
    void test_if_setter_updates_attribute_value() {
        assertEquals(SAVINGS_MINIMUM_BALANCE, savings.getMinBalance());
        savings.setMinBalance(BigDecimal.valueOf(99));
        assertEquals(BigDecimal.valueOf(99), savings.getMinBalance());
    }

    @Test
    @DisplayName("Test if toString() returns the expected string")
    void test_if_toString_returns_expected_string() {
        String expectedStr = "Account [accountId=" + 1000L + ", balance=" + BigDecimal.valueOf(100)
                + ", accountCreationDate=" + null + ", accountType=" + AccountTypeEnum.SAVINGS.toString() + "]"
                + " Savings [minBalance=" + SAVINGS_MINIMUM_BALANCE
                + ", minBalanceCharge=" + MINIMUM_BALANCE_CHARGE.getTransactionFee() + ", interestRate=" + SAVINGS_INTEREST_RATE + "]";
        assertEquals(expectedStr, savings.toString());
    }
}
