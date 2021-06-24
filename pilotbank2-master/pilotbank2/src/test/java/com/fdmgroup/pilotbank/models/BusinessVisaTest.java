package com.fdmgroup.pilotbank.models;

import com.fdmgroup.pilotbank2.models.BusinessVisa;
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
import java.time.LocalDateTime;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class BusinessVisaTest {

    private Customer customer;
    private AccountCreationDTO accountCreationRequest;
    private BusinessVisa businessVisa;
    private LocalDateTime curTime = LocalDateTime.now();

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
                .accountType(AccountTypeEnum.BUSINESS_VISA.toString())
                .balance(BigDecimal.valueOf(100))
                .isMainAccount(true)
                .build();

        businessVisa = BusinessVisa.builder()
                .accountId(1000L)
                .accountType(AccountTypeEnum.valueOf(accountCreationRequest.getAccountType()))
                .ownedAccountCustomer(customer)
                .balance(accountCreationRequest.getBalance())
                .cashBackRate(BUSINESS_VISA_CASH_BACK_RATE)
                .cashBackAmount(BigDecimal.valueOf(0))
                .interestRate(BUSINESS_VISA_INTEREST_RATE)
                .monthlyChargedInterest(BigDecimal.valueOf(0))
                .creditLimit(BUSINESS_VISA_CREDIT_LIMIT)
                .lastRequestedLimitIncrease(LocalDateTime.parse(curTime.toString(), ISO_DATE_TIME))
                .annualFee(BUSINESS_VISA_ANNUAL_FEE)
                .build();
    }

    @Test
    @DisplayName("Test if the business visa has the expected attributes")
    void test_if_first_class_business_visa_has_expected_attributes() {
        assertEquals(BUSINESS_VISA_CASH_BACK_RATE, businessVisa.getCashBackRate());
        assertEquals(BigDecimal.valueOf(0), businessVisa.getCashBackAmount());
        assertEquals(BUSINESS_VISA_INTEREST_RATE, businessVisa.getInterestRate());
        assertEquals(BigDecimal.valueOf(0), businessVisa.getMonthlyChargedInterest());
        assertEquals(BUSINESS_VISA_CREDIT_LIMIT, businessVisa.getCreditLimit());
        assertEquals(LocalDateTime.parse(curTime.toString(), ISO_DATE_TIME), businessVisa.getLastRequestedLimitIncrease());
        assertEquals(BUSINESS_VISA_ANNUAL_FEE, businessVisa.getAnnualFee());
    }

    @Test
    @DisplayName("Test if the no arg constructor creates a business visa with default values")
    void test_if_no_arg_constructor_creates_business_visa_with_default_values() {
        BusinessVisa defaultBusinessVisa = new BusinessVisa();
        assertEquals(BUSINESS_VISA_CASH_BACK_RATE, defaultBusinessVisa.getCashBackRate());
        assertEquals(BigDecimal.valueOf(0), defaultBusinessVisa.getCashBackAmount());
        assertEquals(BUSINESS_VISA_INTEREST_RATE, defaultBusinessVisa.getInterestRate());
        assertEquals(BigDecimal.valueOf(0), defaultBusinessVisa.getMonthlyChargedInterest());
        assertEquals(BUSINESS_VISA_CREDIT_LIMIT, defaultBusinessVisa.getCreditLimit());
        assertEquals(null, defaultBusinessVisa.getLastRequestedLimitIncrease());
        assertEquals(BUSINESS_VISA_ANNUAL_FEE, defaultBusinessVisa.getAnnualFee());
    }

    @Test
    @DisplayName("Test if the all arg constructor creates a business visa with specified values")
    void test_if_all_arg_constructor_creates_business_visa_with_specified_values() {
        BusinessVisa allArgBusinessVisa = new BusinessVisa(BigDecimal.valueOf(10), BigDecimal.valueOf(20),
                                    BigDecimal.valueOf(30), BigDecimal.valueOf(40), BigDecimal.valueOf(50),
                null, BigDecimal.valueOf(60));
        assertEquals(BigDecimal.valueOf(10), allArgBusinessVisa.getCashBackRate());
        assertEquals(BigDecimal.valueOf(20), allArgBusinessVisa.getCashBackAmount());
        assertEquals(BigDecimal.valueOf(30), allArgBusinessVisa.getInterestRate());
        assertEquals(BigDecimal.valueOf(40), allArgBusinessVisa.getMonthlyChargedInterest());
        assertEquals(BigDecimal.valueOf(50), allArgBusinessVisa.getCreditLimit());
        assertEquals(null, allArgBusinessVisa.getLastRequestedLimitIncrease());
        assertEquals(BigDecimal.valueOf(60), allArgBusinessVisa.getAnnualFee());
    }

    @Test
    @DisplayName("Test if the setter updates an attribute value")
    void test_if_setter_updates_attribute_value() {
        assertEquals(BigDecimal.valueOf(0), businessVisa.getCashBackAmount());
        businessVisa.setCashBackAmount(BigDecimal.valueOf(55));
        assertEquals(BigDecimal.valueOf(55), businessVisa.getCashBackAmount());
    }

    @Test
    @DisplayName("Test if toString() returns the expected string")
    void test_if_toString_returns_expected_string() {
        String expectedStr = "Account [accountId=" + 1000L + ", balance=" + BigDecimal.valueOf(100)
                + ", accountCreationDate=" + null + ", accountType=" + AccountTypeEnum.BUSINESS_VISA.toString() + "]"
                + " BusinessVisa [" + "cashBackRate=" + BUSINESS_VISA_CASH_BACK_RATE
                + ", cashBackAmount=" + BigDecimal.valueOf(0)
                + ", interestRate=" + BUSINESS_VISA_INTEREST_RATE
                + ", monthlyChargedInterest=" + BigDecimal.valueOf(0)
                + ", creditLimit=" + BUSINESS_VISA_CREDIT_LIMIT
                + ", lastRequestedLimitIncrease=" + LocalDateTime.parse(curTime.toString(), ISO_DATE_TIME)
                + ", annualFee=" + BUSINESS_VISA_ANNUAL_FEE + "]";
        assertEquals(expectedStr, businessVisa.toString());
    }
}
