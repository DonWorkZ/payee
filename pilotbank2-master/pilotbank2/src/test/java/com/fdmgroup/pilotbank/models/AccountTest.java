package com.fdmgroup.pilotbank.models;

import com.fdmgroup.pilotbank2.models.Account;
import com.fdmgroup.pilotbank2.models.Checking;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.Transaction;
import com.fdmgroup.pilotbank2.models.dto.AccountCreationDTO;
import com.fdmgroup.pilotbank2.services.AccountServiceImpl;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import com.fdmgroup.pilotbank2.type.TransactionTypeEnum;
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

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class AccountTest {

    @Spy
    private AccountServiceImpl mockAccountService;

    private AccountCreationDTO accountCreationRequest;
    private Checking checking;
    private Customer customer;
    private Transaction transaction;
    private List<Transaction> transactionList = new ArrayList<>();

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
                .accountCreationDate(LocalDateTime.parse("2020-02-24T00:00:00", ISO_DATE_TIME))
                .accountType(AccountTypeEnum.valueOf(accountCreationRequest.getAccountType()))
                .ownedAccountCustomer(customer)
                .balance(accountCreationRequest.getBalance())
                .monthlyTransactionsRemaining(CHECKING_MONTHLY_TRANSACTION_LIMIT)
                .hasMonthlyServiceFee(false)
                .isMainAccount(accountCreationRequest.getIsMainAccount())
                .build();

        transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(50L))
                .transactionType(TransactionTypeEnum.CREDIT)
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                .account(checking)
                .build();

        transactionList.add(transaction);
    }

    @Test
    @DisplayName("Test if a newly created account has a correct accountCreationDate")
    void test_if_newly_created_account_has_correct_accountCreationDate() throws Exception {
        doReturn(checking).when(mockAccountService).createAccount(accountCreationRequest);
        mockAccountService.createAccount(accountCreationRequest);
        assertEquals(LocalDateTime.parse("2020-02-24T00:00:00", ISO_DATE_TIME), checking.getAccountCreationDate());
    }

    @Test
    @DisplayName("Test if a newly created account has a correct ownedAccountCustomer")
    void test_if_newly_created_account_has_correct_ownedAccountCustomer() throws Exception {
        doReturn(checking).when(mockAccountService).createAccount(accountCreationRequest);
        mockAccountService.createAccount(accountCreationRequest);
        assertEquals(100L, checking.getOwnedAccountCustomer().getUserId());
    }

    @Test
    @DisplayName("Test if a newly created account has a correct balance value")
    void test_if_newly_created_account_has_correct_balance() throws Exception {
        doReturn(checking).when(mockAccountService).createAccount(accountCreationRequest);
        mockAccountService.createAccount(accountCreationRequest);
        assertEquals(BigDecimal.valueOf(100), checking.getBalance());
    }

    @Test
    @DisplayName("Test if a newly created account has a correct accountType")
    void test_if_newly_created_account_has_correct_accountType() throws Exception {
        doReturn(checking).when(mockAccountService).createAccount(accountCreationRequest);
        mockAccountService.createAccount(accountCreationRequest);
        assertEquals(AccountTypeEnum.CHECKING, checking.getAccountType());
    }

    @Test
    @DisplayName("Test if a newly created account has a correct main account flag")
    void test_if_newly_created_account_has_correct_main_account_flag() throws Exception {
        doReturn(checking).when(mockAccountService).createAccount(accountCreationRequest);
        mockAccountService.createAccount(accountCreationRequest);
        assertTrue(checking.getIsMainAccount());


        accountCreationRequest = AccountCreationDTO.builder()
                .openedByCustomerId(100L)
                .accountType(AccountTypeEnum.CHECKING.toString())
                .balance(BigDecimal.valueOf(100))
                .isMainAccount(false)
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

        doReturn(checking).when(mockAccountService).createAccount(accountCreationRequest);
        mockAccountService.createAccount(accountCreationRequest);
        assertFalse(checking.getIsMainAccount());
    }

    @Test
    @DisplayName("Test if toString() returns the expected string")
    void test_if_toString_returns_expected_string() {
        doReturn(checking).when(mockAccountService).createAccount(accountCreationRequest);
        Account account1 = mockAccountService.createAccount(accountCreationRequest);
        String expectedStr = "Account [accountId=" + 1000L + ", balance=" + BigDecimal.valueOf(100) +
                ", accountCreationDate=" + LocalDateTime.parse("2020-02-24T00:00:00", ISO_DATE_TIME) + ", accountType="
                + AccountTypeEnum.CHECKING + "]" + " Checking [" +
                "monthlyTransactionsRemaining=" + CHECKING_MONTHLY_TRANSACTION_LIMIT +
                ", transactionFee=" + CHECKING_TRANSACTION_FEE +
                ", hasMonthlyServiceFee=" + false +
                ", monthlyServiceFee=" + CHECKING_MONTHLY_FEE +
                ", monthlyMinimumBalance=" + CHECKING_MINIMUM_BALANCE +
                ", eTransferFee=" + CHECKING_E_TRANSFER_FEE +
                "]";
        assertEquals(expectedStr, account1.toString());
    }

    @Test
    @DisplayName("Test if reverseTransaction reverses the gender of a transaction")
    void test_if_reverseTransaction_reverses_gender_of_transaction() {
        checking.reverseTransaction(transaction);
        assertEquals(TransactionTypeEnum.DEBIT, checking.getAllTransactions().get(0).getTransactionType());
    }
}
