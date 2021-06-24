package com.fdmgroup.pilotbank.models;

import com.fdmgroup.pilotbank2.models.BusinessVisa;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.PremiumVisa;
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

@ExtendWith(MockitoExtension.class)
public class PremiumVisaTest {

    private Customer customer;
    private AccountCreationDTO accountCreationRequest;
    private PremiumVisa premiumVisa;
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
                .accountType(AccountTypeEnum.PREMIUM_VISA.toString())
                .balance(BigDecimal.valueOf(100))
                .isMainAccount(true)
                .build();

        premiumVisa = PremiumVisa.builder()
                .accountId(1000L)
                .accountType(AccountTypeEnum.valueOf(accountCreationRequest.getAccountType()))
                .ownedAccountCustomer(customer)
                .balance(accountCreationRequest.getBalance())
                .cashBackRate(PREMIUM_VISA_CASH_BACK_RATE)
                .cashBackAmount(BigDecimal.valueOf(0))
                .interestRate(PREMIUM_VISA_INTEREST_RATE)
                .monthlyChargedInterest(BigDecimal.valueOf(0))
                .creditLimit(PREMIUM_VISA_CREDIT_LIMIT)
                .lastRequestedLimitIncrease(LocalDateTime.parse(curTime.toString(), ISO_DATE_TIME))
                .build();
    }

    @Test
    @DisplayName("Test if the premium visa has the expected attributes")
    void test_if_first_class_premium_visa_has_expected_attributes() {
        assertEquals(PREMIUM_VISA_CASH_BACK_RATE, premiumVisa.getCashBackRate());
        assertEquals(BigDecimal.valueOf(0), premiumVisa.getCashBackAmount());
        assertEquals(PREMIUM_VISA_INTEREST_RATE, premiumVisa.getInterestRate());
        assertEquals(BigDecimal.valueOf(0), premiumVisa.getMonthlyChargedInterest());
        assertEquals(PREMIUM_VISA_CREDIT_LIMIT, premiumVisa.getCreditLimit());
        assertEquals(LocalDateTime.parse(curTime.toString(), ISO_DATE_TIME), premiumVisa.getLastRequestedLimitIncrease());
    }

    @Test
    @DisplayName("Test if the no arg constructor creates a premium visa with default values")
    void test_if_no_arg_constructor_creates_premium_visa_with_default_values() {
        PremiumVisa defaultPremiumVisa = new PremiumVisa();
        assertEquals(PREMIUM_VISA_CASH_BACK_RATE, defaultPremiumVisa.getCashBackRate());
        assertEquals(BigDecimal.valueOf(0), defaultPremiumVisa.getCashBackAmount());
        assertEquals(PREMIUM_VISA_INTEREST_RATE, defaultPremiumVisa.getInterestRate());
        assertEquals(BigDecimal.valueOf(0), defaultPremiumVisa.getMonthlyChargedInterest());
        assertEquals(PREMIUM_VISA_CREDIT_LIMIT, defaultPremiumVisa.getCreditLimit());
        assertEquals(null, defaultPremiumVisa.getLastRequestedLimitIncrease());
    }

    @Test
    @DisplayName("Test if the all arg constructor creates a premium visa with specified values")
    void test_if_all_arg_constructor_creates_premium_visa_with_specified_values() {
        PremiumVisa allArgPremiumVisa = new PremiumVisa(BigDecimal.valueOf(10), BigDecimal.valueOf(20),
                BigDecimal.valueOf(30), BigDecimal.valueOf(40), BigDecimal.valueOf(50), null);
        assertEquals(BigDecimal.valueOf(10), allArgPremiumVisa.getCashBackRate());
        assertEquals(BigDecimal.valueOf(20), allArgPremiumVisa.getCashBackAmount());
        assertEquals(BigDecimal.valueOf(30), allArgPremiumVisa.getInterestRate());
        assertEquals(BigDecimal.valueOf(40), allArgPremiumVisa.getMonthlyChargedInterest());
        assertEquals(BigDecimal.valueOf(50), allArgPremiumVisa.getCreditLimit());
        assertEquals(null, allArgPremiumVisa.getLastRequestedLimitIncrease());
    }

    @Test
    @DisplayName("Test if the setter updates an attribute value")
    void test_if_setter_updates_attribute_value() {
        assertEquals(BigDecimal.valueOf(0), premiumVisa.getCashBackAmount());
        premiumVisa.setCashBackAmount(BigDecimal.valueOf(55));
        assertEquals(BigDecimal.valueOf(55), premiumVisa.getCashBackAmount());
    }

    @Test
    @DisplayName("Test if toString() returns the expected string")
    void test_if_toString_returns_expected_string() {
        String expectedStr = "Account [accountId=" + 1000L + ", balance=" + BigDecimal.valueOf(100)
                + ", accountCreationDate=" + null + ", accountType=" + AccountTypeEnum.PREMIUM_VISA.toString() + "]"
                + " PremiumVisa [" + "cashBackRate=" + PREMIUM_VISA_CASH_BACK_RATE
                + ", cashBackAmount=" + BigDecimal.valueOf(0)
                + ", interestRate=" + PREMIUM_VISA_INTEREST_RATE
                + ", monthlyChargedInterest=" + BigDecimal.valueOf(0)
                + ", creditLimit=" + PREMIUM_VISA_CREDIT_LIMIT
                + ", lastRequestedLimitIncrease=" + LocalDateTime.parse(curTime.toString(), ISO_DATE_TIME)
                + "]";
        assertEquals(expectedStr, premiumVisa.toString());
    }
}
