package com.fdmgroup.pilotbank.models;

import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.FirstClassChecking;
import com.fdmgroup.pilotbank2.models.dto.AccountCreationDTO;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FirstClassCheckingTest {

    private Customer customer;
    private AccountCreationDTO accountCreationRequest;
    private FirstClassChecking firstClassChecking;

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
                .accountType(AccountTypeEnum.FIRST_CLASS_CHECKING.toString())
                .balance(BigDecimal.valueOf(100))
                .isMainAccount(true)
                .build();

        firstClassChecking = FirstClassChecking.builder()
                .accountId(1000L)
                .accountType(AccountTypeEnum.valueOf(accountCreationRequest.getAccountType()))
                .ownedAccountCustomer(customer)
                .balance(accountCreationRequest.getBalance())
                .hasMonthlyServiceFee(false)
                .monthlyServiceFee(FIRST_CLASS_CHECKING_MONTHLY_FEE)
                .monthlyMinimumBalance(FIRST_CLASS_CHECKING_MINIMUM_BALANCE)
                .build();
    }

    @Test
    @DisplayName("Test if the first class checking has the expected attributes")
    void test_if_first_class_checking_has_expected_attributes() {
        assertEquals(false, firstClassChecking.getHasMonthlyServiceFee());
        assertEquals(FIRST_CLASS_CHECKING_MONTHLY_FEE, firstClassChecking.getMonthlyServiceFee());
        assertEquals(FIRST_CLASS_CHECKING_MINIMUM_BALANCE, firstClassChecking.getMonthlyMinimumBalance());
    }

    @Test
    @DisplayName("Test if the no arg constructor creates a first class checking with default values")
    void test_if_no_arg_constructor_creates_first_class_checking_with_default_values() {
        FirstClassChecking defaultFirstClassChecking = new FirstClassChecking();
        assertEquals(false, defaultFirstClassChecking.getHasMonthlyServiceFee());
        assertEquals(FIRST_CLASS_CHECKING_MONTHLY_FEE, defaultFirstClassChecking.getMonthlyServiceFee());
        assertEquals(FIRST_CLASS_CHECKING_MINIMUM_BALANCE, defaultFirstClassChecking.getMonthlyMinimumBalance());
    }

    @Test
    @DisplayName("Test if the all arg constructor creates a first class checking with specified values")
    void test_if_all_arg_constructor_creates_first_class_checking_with_specified_values() {
        FirstClassChecking allArgFirstClassChecking = new FirstClassChecking(true, BigDecimal.valueOf(10),
                                                    BigDecimal.valueOf(20));
        assertEquals(true, allArgFirstClassChecking.getHasMonthlyServiceFee());
        assertEquals(BigDecimal.valueOf(10), allArgFirstClassChecking.getMonthlyServiceFee());
        assertEquals(BigDecimal.valueOf(20), allArgFirstClassChecking.getMonthlyMinimumBalance());
    }

    @Test
    @DisplayName("Test if the setter updates an attribute value")
    void test_if_setter_updates_attribute_value() {
        assertEquals(false, firstClassChecking.getHasMonthlyServiceFee());
        firstClassChecking.setHasMonthlyServiceFee(true);
        assertTrue(firstClassChecking.getHasMonthlyServiceFee());
    }

    @Test
    @DisplayName("Test if toString() returns the expected string")
    void test_if_toString_returns_expected_string() {
        String expectedStr = "Account [accountId=" + 1000L + ", balance=" + BigDecimal.valueOf(100)
                + ", accountCreationDate=" + null + ", accountType=" + AccountTypeEnum.FIRST_CLASS_CHECKING.toString() + "]"
                + " FirstClassChecking [" + "hasMonthlyServiceFee=" + false + ", monthlyServiceFee=" + FIRST_CLASS_CHECKING_MONTHLY_FEE
                + ", monthlyMinimumBalance=" + FIRST_CLASS_CHECKING_MINIMUM_BALANCE + "]";
        assertEquals(expectedStr, firstClassChecking.toString());
    }
}
