package com.fdmgroup.pilotbank.controllers;

import com.fdmgroup.pilotbank2.controllers.AccountController;
import com.fdmgroup.pilotbank2.models.*;
import com.fdmgroup.pilotbank2.models.dto.AccountCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.MainAccountRequestDTO;
import com.fdmgroup.pilotbank2.services.AccountService;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService mockAccountService;

    private Checking checking;
    private Savings savings;
    private Student student;
    private Address address1;
    private Customer customer1;
    private List<Account> accounts = new ArrayList<>();
    private MainAccountRequestDTO mainAccountRequestDTO = new MainAccountRequestDTO();
    private AccountCreationDTO accountCreationDTO = new AccountCreationDTO();

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
                .passwordExpires(LocalDateTime.of(2021, 02, 11, 00, 00, 00, 00)
                        .plusDays(90L))
                .role("CUSTOMER").isActive(true).lastFailedLoginDate(null).accountLockedFlag(false)
                .addressList(Arrays.asList(address1))
                .accountExpires(null).industry("Travel").occupation("Driver").build();

        checking = Checking.builder()
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

        accounts.add(checking);
        accounts.add(savings);

        mainAccountRequestDTO = MainAccountRequestDTO.builder()
                                .oldAccountId(checking.getAccountId())
                                .newAccountId(savings.getAccountId())
                                .build();

        accountCreationDTO = AccountCreationDTO.builder()
                .balance(BigDecimal.valueOf(300L))
                .accountType("STUDENT")
                .isMainAccount(false)
                .build();

        student = Student.builder()
                .accountType(AccountTypeEnum.STUDENT)
                .ownedAccountCustomer(customer1)
                .isMainAccount(false)
                .build();
    }

    @Test
    @DisplayName("test that getAccounts returns an account list")
    void test_getAccounts_returns_account_list() {
        when(mockAccountService.findAll()).thenReturn(accounts);
        ResponseEntity<List<Account>> accountList = accountController.getAccounts();
        verify(mockAccountService, times(1)).findAll();
        assertEquals(2, accountList.getBody().size());
        assertEquals(checking, accountList.getBody().get(0));
        assertEquals(savings, accountList.getBody().get(1));
        assertEquals(HttpStatus.OK, accountList.getStatusCode());
    }

    @Test
    @DisplayName("Test that getAccountById returns an account")
    void test_getAccountById_returns_account() {
        when(mockAccountService.findAccountById(customer1.getUserId())).thenReturn(checking);
        ResponseEntity<?> account = accountController.getAccountById(customer1.getUserId());
        verify(mockAccountService, times(1)).findAccountById(customer1.getUserId());
        assertEquals(checking, account.getBody());
        assertEquals(HttpStatus.OK, account.getStatusCode());
    }

    @Test
    @DisplayName("Test that getAccountById throws exception")
    void test_getAccountById_throws_Exception() {
        when(mockAccountService.findAccountById(customer1.getUserId()))
                .thenThrow(new IllegalArgumentException("IllegalArgumentException") {});
        ResponseEntity<?> account = accountController.getAccountById(customer1.getUserId());

        String expectedMessage = String.format("Error finding account: %s",
                "com.fdmgroup.pilotbank.controllers.AccountControllerTest$1: IllegalArgumentException");
        assertEquals(expectedMessage, account.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, account.getStatusCode());
    }

    @Test
    @DisplayName("Test that setMainAccount returns an account")
    void test_setMainAccount_returns_account() {
        when(mockAccountService.setAsMainAccount(mainAccountRequestDTO)).thenReturn(savings);
        ResponseEntity<?> account = accountController.setMainAccount(mainAccountRequestDTO);
        verify(mockAccountService, times(1)).setAsMainAccount(mainAccountRequestDTO);
        assertEquals(savings, account.getBody());
        assertEquals(HttpStatus.OK, account.getStatusCode());
    }

    @Test
    @DisplayName("Test that setMainAccount throws exception")
    void test_setMainAccount_throws_Exception() {
        when(mockAccountService.setAsMainAccount(mainAccountRequestDTO))
                .thenThrow(new IllegalStateException("IllegalStateException") {});
        ResponseEntity<?> account = accountController.setMainAccount(mainAccountRequestDTO);

        String expectedMessage = String.format("Error setting account: %s as main account",
                "com.fdmgroup.pilotbank.controllers.AccountControllerTest$2: IllegalStateException");
        assertEquals(expectedMessage, account.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, account.getStatusCode());
    }

    @Test
    @DisplayName("Test that createAccount returns a new account")
    void test_createAccount_returns_newAccount() {
        when(mockAccountService.createAccount(accountCreationDTO)).thenReturn(student);
        ResponseEntity<?> account = accountController.createAccount(accountCreationDTO);
        verify(mockAccountService, times(1)).createAccount(accountCreationDTO);
        assertEquals(student, account.getBody());
        assertEquals(HttpStatus.CREATED, account.getStatusCode());
    }

    @Test
    @DisplayName("Test that createAccount throws exception")
    void test_createAccount_throws_Exception() {
        when(mockAccountService.createAccount(accountCreationDTO))
                .thenThrow(new IllegalArgumentException("IllegalArgumentException") {});
        ResponseEntity<?> account = accountController.createAccount(accountCreationDTO);

        String expectedMessage = String.format("Error while trying to create account: %s",
                "com.fdmgroup.pilotbank.controllers.AccountControllerTest$3: IllegalArgumentException");
        assertEquals(expectedMessage, account.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, account.getStatusCode());
    }

}
