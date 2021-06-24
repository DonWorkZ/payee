package com.fdmgroup.pilotbank.services;

import com.fdmgroup.pilotbank2.models.*;
import com.fdmgroup.pilotbank2.models.dto.AccountCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.MainAccountRequestDTO;
import com.fdmgroup.pilotbank2.repo.AccountRepo;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import com.fdmgroup.pilotbank2.services.AccountServiceImpl;
import com.fdmgroup.pilotbank2.services.UserServiceImpl;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith({MockitoExtension.class})
public class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepo mockAccountRepo;

    @Mock
    private UserRepo mockUserRepo;

    private AccountCreationDTO accountCreationDTO;
    private MainAccountRequestDTO mainAccountRequestDTO;
    private Address address;
    private Customer customer;
    private Checking checking;
    private Savings newSavings;

    @BeforeEach
    void init() {
        address = Address.builder()
                .streetNumber("123").streetName("Wall Street").suiteNumber(null)
                .city("New York").province("NY").postalCode("11111").build();

        customer = Customer.builder()
                .userId(999L).title("Mr.").firstName("Test").lastName("User")
                .username("Experiment999")
                .email("experiment999@email.com")
                .phoneNumber("626-626-6266")
                .sin("{bcrypt}$2a$10$mWkWlyaB89vtZPwRk0Gb1u3GZK08mDDc04tL/2Air0RFz.j0.2MR6")
                .password("{bcrypt}$2a$10$flvQjNBf1Fwd3ETm6Ttf8eVKPhvSzCGGj5xYBKOLTqRtwdmqwlGDu")
                .passwordExpires(LocalDateTime.of(2021,02,10,00,00,00,00)
                        .plusDays(90L))
                .role("CUSTOMER").isActive(true).lastFailedLoginDate(null).accountLockedFlag(false)
                .addressList(Arrays.asList(address))
                .accountExpires(null).industry("Travel").occupation("Driver").build();

        accountCreationDTO = AccountCreationDTO.builder()
                .openedByCustomerId(999L)
                .accountType(AccountTypeEnum.CHECKING.toString())
                .balance(BigDecimal.valueOf(450))
                .isMainAccount(false)
                .build();

        checking = Checking.builder()
                .accountId(1111L)
                .isMainAccount(true)
                .accountType(AccountTypeEnum.valueOf(accountCreationDTO.getAccountType()))
                .balance(accountCreationDTO.getBalance())
                .allTransactions(new ArrayList<>())
                .ownedAccountCustomer(customer)
                .openedByCustomer(customer)
                .build();

        customer.setMainAccount(checking);

        newSavings = Savings.builder()
                .accountId(2222L)
                .isMainAccount(false)
                .accountType(AccountTypeEnum.SAVINGS)
                .balance(BigDecimal.valueOf(1000l))
                .allTransactions(new ArrayList<>())
                .ownedAccountCustomer(customer)
                .openedByCustomer(customer)
                .build();

        mainAccountRequestDTO = MainAccountRequestDTO.builder()
                                .oldAccountId(checking.getAccountId())
                                .newAccountId(newSavings.getAccountId())
                                .build();
    }

    @Test
    @DisplayName("Test providing a correct accountId returns an account")
    void testProvidingACorrectAccountIdReturnsAnAccount(){
        when(mockAccountRepo.findByAccountId(checking.getAccountId())).thenReturn(Optional.of(checking));
        accountService.findAccountById(checking.getAccountId());
        verify(mockAccountRepo, times(1)).findByAccountId(checking.getAccountId());
        assertDoesNotThrow(()-> mockAccountRepo.findByAccountId(checking.getAccountId()));
    }

    @Test
    @DisplayName("Test providing an incorrect accountId throws an IllegalArgumentException")
    void testProvidingInvalidAccountIdThrowsIllegalArgumentException(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> accountService.findAccountById(42L));

        String expectedMessage = String.format("Account with ID %s not found", 42L);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test that setAsMainAccount correctly updates the main account")
    void testThatSetAsMainAccountCorrectlyUpdatesMainAccount() {
        assertEquals(checking, customer.getMainAccount());
        assertTrue(checking.getIsMainAccount());
        assertFalse(newSavings.getIsMainAccount());

        when(mockAccountRepo.findByAccountId(checking.getAccountId())).thenReturn(Optional.of(checking));
        when(mockAccountRepo.findByAccountId(newSavings.getAccountId())).thenReturn(Optional.of(newSavings));
        when(mockUserRepo.findByUserId(checking.getOpenedByCustomer().getUserId())).thenReturn(Optional.of(customer));

        accountService.setAsMainAccount(mainAccountRequestDTO);

        verify(mockAccountRepo, times(1)).findByAccountId(checking.getAccountId());
        verify(mockAccountRepo, times(1)).findByAccountId(newSavings.getAccountId());
        verify(mockUserRepo, times(1)).findByUserId(checking.getOpenedByCustomer().getUserId());

        assertEquals(newSavings, customer.getMainAccount());
        assertTrue(newSavings.getIsMainAccount());
        assertFalse(checking.getIsMainAccount());
    }

    @Test
    @DisplayName("Test providing an incorrect old main accountId throws an IllegalStateException")
    void testProvidingInvalidOldMainAccountIdThrowsIllegalStateException(){
        MainAccountRequestDTO badMainAccountRequestDTO = MainAccountRequestDTO.builder()
                                                            .oldAccountId(852L)
                                                            .newAccountId(newSavings.getAccountId())
                                                            .build();
        Exception exception = assertThrows(IllegalStateException.class, () -> accountService.setAsMainAccount(badMainAccountRequestDTO));
        String expectedMessage = String.format("Previous main account with ID %s not found", 852L);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test providing an incorrect new main accountId throws an IllegalStateException")
    void testProvidingInvalidNewMainAccountIdThrowsIllegalStateException(){
        MainAccountRequestDTO badMainAccountRequestDTO = MainAccountRequestDTO.builder()
                                                            .oldAccountId(checking.getAccountId())
                                                            .newAccountId(741L)
                                                            .build();
        when(mockAccountRepo.findByAccountId(checking.getAccountId())).thenReturn(Optional.of(checking));

        Exception exception = assertThrows(IllegalStateException.class, () -> accountService.setAsMainAccount(badMainAccountRequestDTO));
        String expectedMessage = String.format("New main account with ID %s not found", 741L);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test providing an incorrect userId throws an IllegalStateException")
    void testProvidingInvalidUserIdThrowsIllegalStateException(){
        MainAccountRequestDTO badMainAccountRequestDTO = MainAccountRequestDTO.builder()
                .oldAccountId(checking.getAccountId())
                .newAccountId(newSavings.getAccountId())
                .build();

        Customer badCustomer = Customer.builder()
                .userId(444L).title("Mr.").firstName("Test").lastName("User")
                .username("BadUser999")
                .email("experiment999@email.com")
                .phoneNumber("626-626-6266")
                .sin("{bcrypt}$2a$10$mWkWlyaB89vtZPwRk0Gb1u3GZK08mDDc04tL/2Air0RFz.j0.2MR6")
                .password("{bcrypt}$2a$10$flvQjNBf1Fwd3ETm6Ttf8eVKPhvSzCGGj5xYBKOLTqRtwdmqwlGDu")
                .passwordExpires(LocalDateTime.of(2021,02,10,00,00,00,00)
                        .plusDays(90L))
                .role("CUSTOMER").isActive(true).lastFailedLoginDate(null).accountLockedFlag(false)
                .addressList(Arrays.asList(address))
                .accountExpires(null).industry("Travel").occupation("Driver").build();
        checking.setOpenedByCustomer(badCustomer);

        when(mockAccountRepo.findByAccountId(checking.getAccountId())).thenReturn(Optional.of(checking));
        when(mockAccountRepo.findByAccountId(newSavings.getAccountId())).thenReturn(Optional.of(newSavings));

        Exception exception = assertThrows(IllegalStateException.class, () -> accountService.setAsMainAccount(badMainAccountRequestDTO));
        String expectedMessage = String.format("Customer with ID: %s not found", 444L);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test that findAccountByCustomer returns the customer's accounts")
    void testFindAccountByCustomerReturnsCustomerAccounts() {
        when(mockAccountRepo.findByOwnedAccountCustomer(customer)).thenReturn(Arrays.asList(checking));
        List<Account> accountList = accountService.findAccountByCustomer(customer);
        verify(mockAccountRepo, times(1)).findByOwnedAccountCustomer(customer);
        assertEquals(1, accountList.size());
        assertEquals(checking, accountList.get(0));
    }

    @Test
    @DisplayName("Test that findAll returns a list of accounts")
    void testFindAllReturnsAccountList() {
        when(mockAccountRepo.findAll()).thenReturn(Arrays.asList(checking));
        List<Account> accountList = accountService.findAll();
        verify(mockAccountRepo, times(1)).findAll();
        assertEquals(1, accountList.size());
        assertEquals(checking, accountList.get(0));
    }

    @Test
    @DisplayName("Test that createAccount successfully creates a new account")
    void testCreateAccountCreatesNewAccount() {
        AccountCreationDTO newAccountCreationReq = AccountCreationDTO.builder()
                .openedByCustomerId(999L)
                .accountType("FIRST CLASS CHEQUING")
                .balance(BigDecimal.valueOf(500))
                .isMainAccount(false)
                .build();
        when(mockUserRepo.findById(newAccountCreationReq.getOpenedByCustomerId())).thenReturn(Optional.of(customer));

        Account newAccount = accountService.createAccount(newAccountCreationReq);

        assertEquals(AccountTypeEnum.FIRST_CLASS_CHECKING, newAccount.getAccountType());
        assertEquals(BigDecimal.valueOf(500), newAccount.getBalance());

        newAccountCreationReq = AccountCreationDTO.builder()
                .openedByCustomerId(999L)
                .accountType("CHECKING")
                .balance(BigDecimal.valueOf(600))
                .isMainAccount(false)
                .build();

        newAccount = accountService.createAccount(newAccountCreationReq);

        assertEquals(AccountTypeEnum.CHECKING, newAccount.getAccountType());
        assertEquals(BigDecimal.valueOf(600), newAccount.getBalance());

        newAccountCreationReq = AccountCreationDTO.builder()
                .openedByCustomerId(999L)
                .accountType("SAVINGS")
                .balance(BigDecimal.valueOf(700))
                .isMainAccount(false)
                .build();

        newAccount = accountService.createAccount(newAccountCreationReq);

        assertEquals(AccountTypeEnum.SAVINGS, newAccount.getAccountType());
        assertEquals(BigDecimal.valueOf(700), newAccount.getBalance());

        newAccountCreationReq = AccountCreationDTO.builder()
                .openedByCustomerId(999L)
                .accountType("STUDENT")
                .balance(BigDecimal.valueOf(800))
                .isMainAccount(false)
                .build();

        newAccount = accountService.createAccount(newAccountCreationReq);

        assertEquals(AccountTypeEnum.STUDENT, newAccount.getAccountType());
        assertEquals(BigDecimal.valueOf(800), newAccount.getBalance());

        newAccountCreationReq = AccountCreationDTO.builder()
                .openedByCustomerId(999L)
                .accountType("BUSINESS VISA")
                .balance(BigDecimal.valueOf(900))
                .isMainAccount(false)
                .build();

        newAccount = accountService.createAccount(newAccountCreationReq);

        assertEquals(AccountTypeEnum.BUSINESS_VISA, newAccount.getAccountType());
        assertEquals(BigDecimal.valueOf(0), newAccount.getBalance());
    }

    @Test
    @DisplayName("Test that a Visa account's balance starts with 0")
    void testVisaAccountBalanceStartsWithZero() {
        AccountCreationDTO newAccountCreationReq = AccountCreationDTO.builder()
                .openedByCustomerId(999L)
                .accountType("PREMIUM VISA")
                .balance(BigDecimal.valueOf(200))
                .isMainAccount(false)
                .build();
        when(mockUserRepo.findById(newAccountCreationReq.getOpenedByCustomerId())).thenReturn(Optional.of(customer));

        Account newAccount = accountService.createAccount(newAccountCreationReq);

        assertEquals(AccountTypeEnum.PREMIUM_VISA, newAccount.getAccountType());
        assertEquals(BigDecimal.valueOf(0), newAccount.getBalance());
    }

    @Test
    @DisplayName("Test providing an incorrect userId throws an IllegalArgumentException")
    void testProvidingInvalidUserIdThrowsIllegalArgumentException(){
        AccountCreationDTO badAccountCreationReq = AccountCreationDTO.builder()
                .openedByCustomerId(666L)
                .accountType("STUDENT")
                .balance(BigDecimal.valueOf(50))
                .isMainAccount(false)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(badAccountCreationReq));

        String expectedMessage = String.format("Customer with ID %s not found", 666L);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test providing an incorrect account type throws an IllegalArgumentException")
    void testProvidingInvalidAccountTypeThrowsIllegalArgumentException(){
        AccountCreationDTO badAccountCreationReq = AccountCreationDTO.builder()
                .openedByCustomerId(999L)
                .accountType("RANDOM")
                .balance(BigDecimal.valueOf(300))
                .isMainAccount(false)
                .build();

        when(mockUserRepo.findById(badAccountCreationReq.getOpenedByCustomerId())).thenReturn(Optional.of(customer));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(badAccountCreationReq));

        String expectedMessage = String.format("Account type %s not defined", "RANDOM");
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

}
