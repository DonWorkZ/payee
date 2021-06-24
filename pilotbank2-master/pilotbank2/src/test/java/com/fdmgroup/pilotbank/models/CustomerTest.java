package com.fdmgroup.pilotbank.models;

import com.fdmgroup.pilotbank2.models.*;
import com.fdmgroup.pilotbank2.models.dto.AccountCreationDTO;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.*;
import static com.fdmgroup.pilotbank2.common.PilotBankConstants.SAVINGS_INTEREST_RATE;
import static com.fdmgroup.pilotbank2.type.TransactionMemoEnum.MINIMUM_BALANCE_CHARGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerTest {

    private Customer customer;
    private AccountCreationDTO accountCreationRequest;
    private Checking checking;
    private Savings savings;
    private List<Account> accountList = new ArrayList<>();
    private Payee payee, newPayee;

    @BeforeEach
    void init() {
        payee = new Payee();
        newPayee = new Payee();

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

        accountList.add(checking);

        savings = Savings.builder()
                .accountId(1000L)
                .accountType(AccountTypeEnum.valueOf(accountCreationRequest.getAccountType()))
                .ownedAccountCustomer(customer)
                .balance(accountCreationRequest.getBalance())
                .minBalance(SAVINGS_MINIMUM_BALANCE)
                .minBalanceCharge(MINIMUM_BALANCE_CHARGE.getTransactionFee())
                .interestRate(SAVINGS_INTEREST_RATE)
                .build();

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
                .income(BigDecimal.valueOf(50000))
                .mainAccount(checking)
                .ownedAccounts(accountList)
                .payees(Arrays.asList(payee))
                .build();
    }

    @Test
    @DisplayName("Test if the customer has the expected attributes")
    void test_if_first_class_customer_has_expected_attributes() {
        assertEquals(BigDecimal.valueOf(50000), customer.getIncome());
        assertEquals(checking, customer.getMainAccount());
        assertEquals(checking, customer.getOwnedAccounts().get(0));
        assertEquals(1, customer.getOwnedAccounts().size());
        assertEquals(payee, customer.getPayees().get(0));
        assertEquals(1, customer.getPayees().size());
    }

    @Test
    @DisplayName("Test if the all arg constructor creates a customer with default values")
    void test_if_all_arg_constructor_creates_customer_with_specified_values() {
        Customer allArgCustomer= new Customer(BigDecimal.valueOf(100000), savings, Arrays.asList(savings), Arrays.asList(newPayee));
        assertEquals(BigDecimal.valueOf(100000), allArgCustomer.getIncome());
        assertEquals(savings, allArgCustomer.getMainAccount());
        assertEquals(savings, allArgCustomer.getOwnedAccounts().get(0));
        assertEquals(1, allArgCustomer.getOwnedAccounts().size());
        assertEquals(newPayee, allArgCustomer.getPayees().get(0));
        assertEquals(1, allArgCustomer.getPayees().size());
    }

    @Test
    @DisplayName("Test if removeFromOwnedAccounts removes an account from the customer")
    void test_if_removeFromOwnedAccounts_removes_account_from_customer() {
        assertEquals(1, customer.getOwnedAccounts().size());
        customer.removeFromOwnedAccounts(checking);
        assertEquals(0, customer.getOwnedAccounts().size());
    }
}
