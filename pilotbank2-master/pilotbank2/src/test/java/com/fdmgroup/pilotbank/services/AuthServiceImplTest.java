package com.fdmgroup.pilotbank.services;

import com.fdmgroup.pilotbank2.models.*;
import com.fdmgroup.pilotbank2.models.dto.*;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import com.fdmgroup.pilotbank2.services.AuthServiceImpl;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import com.fdmgroup.pilotbank2.type.TransactionMemoEnum;
import com.fdmgroup.pilotbank2.type.TransactionTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.ISO_DATE_TIME;
import static com.fdmgroup.pilotbank2.common.PilotBankConstants.PASSWORD_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepo mockUserRepo;

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @Mock
    private JavaMailSender mockMailSender;

    private AccountCreationDTO accountCreationDTO;
    private Address address, newAddress;
    private Checking checking;
    private Savings savings;
    private Student student;
    private FirstClassChecking firstClassChecking;
    private PremiumVisa premiumVisa;
    private BusinessVisa businessVisa;
    private Customer customer;
    private CustomerCreationDTO customerCreationRequest;
    private UserUpdateDTO userUpdate;
    private Transaction transaction;
    private UsernameDTO usernameDTO;
    private SecurityAnswerDTO securityAnswerDTO;
    private SecurityCodeRequestDTO securityCodeRequestDTO;
    private SecurityCodeVerificationDTO securityCodeVerificationDTO;
    private PasswordUpdateDTO passwordUpdateDTO;
    private HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    void init() {

        address = Address.builder()
                .streetNumber("626").streetName("Model Citizen Ln.").suiteNumber(null)
                .city("Oahu").province("Hawaii").postalCode("62626").build();

        accountCreationDTO = AccountCreationDTO.builder()
                .openedByCustomerId(5L).accountType(AccountTypeEnum.CHECKING.toString())
                .balance(BigDecimal.valueOf(450)).isMainAccount(true)
                .build();

        customerCreationRequest = CustomerCreationDTO.builder()
                .username("Experiment626").title("Mr.").firstName("Stitch").lastName("Pelakai")
                .email("experiment626@gmail.com").phoneNumber("626-626-6266").password("1A2b3c4!")
                .role("CUSTOMER").sin("626-626-626").industry("Entertainment")
                .occupation("Elvis Impersonator")
                .address(address)
                .account(accountCreationDTO)
                .build();

        checking = Checking.builder()
                .accountId(5L)
                .isMainAccount(true)
                .accountType(AccountTypeEnum.CHECKING)
                .balance(customerCreationRequest.getAccount().getBalance())
                .allTransactions(new ArrayList<>())
                .ownedAccountCustomer(customer)
                .build();

        savings = Savings.builder()
                .accountId(6L)
                .accountType(AccountTypeEnum.SAVINGS)
                .balance(customerCreationRequest.getAccount().getBalance())
                .ownedAccountCustomer(customer)
                .isMainAccount(true)
                .allTransactions(new ArrayList<>())
                .build();

        student = Student.builder()
                .accountId(7L)
                .accountType(AccountTypeEnum.STUDENT)
                .balance(customerCreationRequest.getAccount().getBalance())
                .ownedAccountCustomer(customer)
                .isMainAccount(true)
                .allTransactions(new ArrayList<>())
                .build();

        firstClassChecking = FirstClassChecking.builder()
                .accountId(8L)
                .accountType(AccountTypeEnum.FIRST_CLASS_CHECKING)
                .balance(customerCreationRequest.getAccount().getBalance())
                .ownedAccountCustomer(customer)
                .isMainAccount(true)
                .allTransactions(new ArrayList<>())
                .build();

        premiumVisa = PremiumVisa.builder()
                .accountId(9L)
                .accountType(AccountTypeEnum.PREMIUM_VISA)
                .ownedAccountCustomer(customer)
                .isMainAccount(true)
                .allTransactions(new ArrayList<>())
                .build();

        businessVisa = BusinessVisa.builder()
                .accountId(0L)
                .accountType(AccountTypeEnum.BUSINESS_VISA)
                .ownedAccountCustomer(customer)
                .isMainAccount(true)
                .allTransactions(new ArrayList<>())
                .build();

        transaction = Transaction.builder()
                .account(checking)
                .transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
                .amount(checking.getBalance())
                .transactionType(TransactionTypeEnum.CREDIT)
                .transactionMemo(TransactionMemoEnum.INITIAL_DEPOSIT)
                .build();

        customer = Customer.builder()
                .userId(626L).title("Mr.").firstName("Stitch").lastName("Pelakai").username("Experiment626")
                .email("experiment626@gmail.com").phoneNumber("626-626-6266")
                .sin("{bcrypt}$2a$10$mWkWlyaB89vtZPwRk0Gb1u3GZK08mDDc04tL/2Air0RFz.j0.2MR6")
                .password("{bcrypt}$2a$10$flvQjNBf1Fwd3ETm6Ttf8eVKPhvSzCGGj5xYBKOLTqRtwdmqwlGDu")
                .passwordExpires(LocalDateTime.of(2021,02,02,00,00,00,00)
                        .plusDays(90L))
                .tempLockOutExpiration(LocalDateTime.parse("1970-01-01T00:00:00"))
                .role("CUSTOMER").isActive(true).lastFailedLoginDate(null).accountLockedFlag(false)
                .securityQuestion("What is your favorite food?").securityAnswer("pizza")
                .addressList(Arrays.asList(address)).securityCode(1234)
                .accountExpires(null).industry("Entertainment").occupation("Elvis Impersonator").build();

        newAddress = Address.builder()
                .streetNumber("888").streetName("New User Way").suiteNumber("222")
                .city("New York").province("NY").postalCode("10000").build();

        userUpdate = UserUpdateDTO.builder()
                .email("cuteandfluffy@gmail.com")
                .build();

        usernameDTO = UsernameDTO.builder()
                .username(customer.getUsername())
                .build();

        securityAnswerDTO = SecurityAnswerDTO.builder()
                .username(customer.getUsername())
                .answer(customer.getSecurityAnswer())
                .build();

        securityCodeRequestDTO = SecurityCodeRequestDTO.builder()
                .username(customer.getUsername())
                //.emailOrText("email")
                //.contactInfo("experiment626@gmail.com")
                .build();

        securityCodeVerificationDTO = SecurityCodeVerificationDTO.builder()
                .username(customer.getUsername())
                .securityCode("1234")
                .build();

        passwordUpdateDTO = PasswordUpdateDTO.builder()
                .username(customer.getUsername())
                .password("newPW123!")
                .build();
    }

    @Test
    @DisplayName("Test that a User can authenticate")
    void testThatAUserCanAuthenticate(){
        when(mockUserRepo.existsByUsernameAndPassword(customer.getUsername(), customer.getPassword())).thenReturn(true);
        authService.authentication(customer.getUsername(), customer.getPassword());
        verify(mockUserRepo, times(1)).existsByUsernameAndPassword(customer.getUsername(), customer.getPassword());
    }

    @Test
    @DisplayName("Test that a User can login")
    void testThatAUserCanLogin(){
        when(mockUserRepo.findByUsernameAndPassword(customer.getUsername(), customer.getPassword())).thenReturn(customer);
        authService.login(customer.getUsername(), customer.getPassword());
        verify(mockUserRepo, times(1)).findByUsernameAndPassword(customer.getUsername(), customer.getPassword());
    }

    @Test
    @DisplayName("Test authorizeDevice returns an array of a success message string and masked contact info")
    void testThatAuthorizeDeviceReturnsArrayOfSuccessMessageStringAndMaskedContactInfo() {
        customer.setDeviceInfo("testDeviceInfo");
        customer.setEmail("ab@gmail.com");
        customer.setPhoneNumber("1234");
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));

        headers.set("User-Agent", "testDeviceInfo");
        List<String> resStr = authService.authorizeDevice(usernameDTO, headers);
        verify(mockUserRepo, times(1)).findByUsername(customer.getUsername());

        assertEquals(3, resStr.size());
        assertEquals("Device Authorization Success", resStr.get(0));
        assertEquals("a*@*****.com", resStr.get(1));
        assertEquals("1**4", resStr.get(2));
    }

    @Test
    @DisplayName("Test authorizeDevice returns an array of a success message string and masked contact info2")
    void testThatAuthorizeDeviceReturnsArrayOfSuccessMessageStringAndMaskedContactInfo2() {
        customer.setDeviceInfo("testDeviceInfo");
        customer.setEmail("a@gmail.com");
        customer.setPhoneNumber("911");
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));

        headers.set("User-Agent", "testDeviceInfo");
        List<String> resStr = authService.authorizeDevice(usernameDTO, headers);
        verify(mockUserRepo, times(1)).findByUsername(customer.getUsername());

        assertEquals(3, resStr.size());
        assertEquals("Device Authorization Success", resStr.get(0));
        assertEquals("*@*****.com", resStr.get(1));
        assertEquals("9**", resStr.get(2));
    }

    @Test
    @DisplayName("Test authorizeDevice returns an array of a success message string and masked contact info3")
    void testThatAuthorizeDeviceReturnsArrayOfSuccessMessageStringAndMaskedContactInfo3() {
        customer.setDeviceInfo("testDeviceInfo");
        customer.setEmail("abc@gmail.com");
        customer.setPhoneNumber("12345");
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));

        headers.set("User-Agent", "testDeviceInfo");
        List<String> resStr = authService.authorizeDevice(usernameDTO, headers);
        verify(mockUserRepo, times(1)).findByUsername(customer.getUsername());

        assertEquals(3, resStr.size());
        assertEquals("Device Authorization Success", resStr.get(0));
        assertEquals("a**@*****.com", resStr.get(1));
        assertEquals("12**5", resStr.get(2));
    }

    @Test
    @DisplayName("Test authorizeDevice returns a failed message string")
    void testThatAuthorizeDeviceReturnsFailedMessageString() {
        customer.setDeviceInfo("testDeviceInfo");
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));

        headers.set("User-Agent", "incorrectDeviceInfo");
        List<String> resStr = authService.authorizeDevice(usernameDTO, headers);
        verify(mockUserRepo, times(1)).findByUsername(customer.getUsername());
        assertEquals("Device Authorization Failed", resStr.get(0));
    }

    @Test
    @DisplayName("Test authorizeDevice throws IllegalArgumentException")
    void testThatAuthorizeDeviceThrowsIllegalArgumentException() {
        UsernameDTO invalidUsernameDTO = new UsernameDTO();
        Exception exception = assertThrows(
                IllegalArgumentException.class, ()-> authService.authorizeDevice(invalidUsernameDTO, headers)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(String.format("User with username %s not found!", null)));
    }

    @Test
    @DisplayName("Test authorizeDevice throws NullPointerException")
    void testThatAuthorizeDeviceThrowsNullPointerException() {
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        Exception exception = assertThrows(NullPointerException.class, ()-> authService.authorizeDevice(usernameDTO, null));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("HttpHeaders should not be null!"));
    }

    @Test
    @DisplayName("Test authorizeDevice throws NullPointerException2")
    void testThatAuthorizeDeviceThrowsNullPointerException2() {
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        Exception exception = assertThrows(NullPointerException.class, ()-> authService.authorizeDevice(usernameDTO, headers));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("User-Agent header should not be null!"));
    }

    @Test
    @DisplayName("Test authorizeDevice throws NullPointerException3")
    void testThatAuthorizeDeviceThrowsNullPointerException3() {
        customer.setEmail(null);
        customer.setDeviceInfo("testDeviceInfo");
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        headers.set("User-Agent", "testDeviceInfo");
        Exception exception = assertThrows(NullPointerException.class, ()-> authService.authorizeDevice(usernameDTO, headers));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Email must not be null!"));
    }

    @Test
    @DisplayName("Test authorizeDevice throws NullPointerException4")
    void testThatAuthorizeDeviceThrowsNullPointerException4() {
        customer.setPhoneNumber(null);
        customer.setDeviceInfo("testDeviceInfo");
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        headers.set("User-Agent", "testDeviceInfo");
        Exception exception = assertThrows(NullPointerException.class, ()-> authService.authorizeDevice(usernameDTO, headers));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Phone number must not be null!"));
    }

    @Test
    @DisplayName("Test authorizeDevice throws NullPointerException5")
    void testThatAuthorizeDeviceThrowsNullPointerException5() {
        customer.setEmail("@invalid.mail");
        customer.setDeviceInfo("testDeviceInfo");
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        headers.set("User-Agent", "testDeviceInfo");
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> authService.authorizeDevice(usernameDTO, headers));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("The email address's format is invalid!"));
    }

    @Test
    @DisplayName("Test authorizeDevice throws NullPointerException6")
    void testThatAuthorizeDeviceThrowsNullPointerException6() {
        customer.setEmail("invalid@");
        customer.setDeviceInfo("testDeviceInfo");
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        headers.set("User-Agent", "testDeviceInfo");
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> authService.authorizeDevice(usernameDTO, headers));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("The email address's format is invalid!"));
    }

    @Test
    @DisplayName("Test authorizeDevice throws NullPointerException7")
    void testThatAuthorizeDeviceThrowsNullPointerException7() {
        customer.setEmail("invalid@com");
        customer.setDeviceInfo("testDeviceInfo");
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        headers.set("User-Agent", "testDeviceInfo");
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> authService.authorizeDevice(usernameDTO, headers));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("The email address's format is invalid!"));
    }

    @Test
    @DisplayName("Test authorizeDevice throws NullPointerException8")
    void testThatAuthorizeDeviceThrowsNullPointerException8() {
        customer.setEmail("invalid@.com");
        customer.setDeviceInfo("testDeviceInfo");
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        headers.set("User-Agent", "testDeviceInfo");
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> authService.authorizeDevice(usernameDTO, headers));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("The email address's format is invalid!"));
    }

    @Test
    @DisplayName("Test authorizeDevice throws NullPointerException9")
    void testThatAuthorizeDeviceThrowsNullPointerException9() {
        customer.setEmail("invalid@email.");
        customer.setDeviceInfo("testDeviceInfo");
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        headers.set("User-Agent", "testDeviceInfo");
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> authService.authorizeDevice(usernameDTO, headers));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("The email address's format is invalid!"));
    }

    @Test
    @DisplayName("Test authorizeDevice throws NullPointerException10")
    void testThatAuthorizeDeviceThrowsNullPointerException10() {
        customer.setPhoneNumber("01");
        customer.setDeviceInfo("testDeviceInfo");
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        headers.set("User-Agent", "testDeviceInfo");
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> authService.authorizeDevice(usernameDTO, headers));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("The phone number is too short!"));
    }

    @Test
    @DisplayName("Test fetchSecurityQuestion returns a security question")
    void testThatFetchSecurityQuestionReturnsSecurityQuestion() {
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        String securityQuestion = authService.fetchSecurityQuestion(usernameDTO);
        verify(mockUserRepo, times(1)).findByUsername(customer.getUsername());
        assertEquals("What is your favorite food?", securityQuestion);
    }

    @Test
    @DisplayName("Test fetchSecurityQuestion throws IllegalArgumentException")
    void testThatFetchSecurityQuestionThrowsIllegalArgumentException() {
        UsernameDTO invalidUsernameDTO = new UsernameDTO();
        Exception exception = assertThrows(
                IllegalArgumentException.class, ()-> authService.fetchSecurityQuestion(invalidUsernameDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(String.format("User with username %s not found!", null)));
    }

    @Test
    @DisplayName("Test fetchSecurityQuestion throws IllegalStateException")
    void testThatFetchSecurityQuestionThrowsIllegalStateException() {
        customer.setAccountLockedFlag(true);
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        Exception exception = assertThrows(
                IllegalStateException.class, ()-> authService.fetchSecurityQuestion(usernameDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Account is locked. Please contact our administration."));
    }

    @Test
    @DisplayName("Test fetchSecurityQuestion throws IllegalStateException2")
    void testThatFetchSecurityQuestionThrowsIllegalStateException2() {
        customer.setTempLockOutExpiration(LocalDateTime.now().plusSeconds(1L));
        when(mockUserRepo.findByUsername(usernameDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        Exception exception = assertThrows(
                IllegalStateException.class, ()-> authService.fetchSecurityQuestion(usernameDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(String.format(
                "Account is temporarily locked. Please wait till %s.", customer.getTempLockOutExpiration().toString()
        )));
    }

    @Test
    @DisplayName("Test verifySecurityAnswer returns a success message string")
    void testThatVerifySecurityAnswerReturnsSuccessMessageString() {
        when(mockUserRepo.findByUsername(securityAnswerDTO.getUsername())).thenReturn(Optional.ofNullable(customer));

        List<String> resStr = authService.verifySecurityAnswer(securityAnswerDTO);
        verify(mockUserRepo, times(1)).findByUsername(securityAnswerDTO.getUsername());
        assertEquals(3, resStr.size());
        assertEquals("Correct Answer", resStr.get(0));
        assertEquals("e************@*****.com", resStr.get(1));
        assertEquals("62*********6", resStr.get(2));
    }

    @Test
    @DisplayName("Test verifySecurityAnswer returns the first attempt failed message string")
    void testThatVerifySecurityAnswerReturnsFirstAttemptFailedMessageString() {
        customer.setSecurityAnswer("spaghetti");
        when(mockUserRepo.findByUsername(securityAnswerDTO.getUsername())).thenReturn(Optional.ofNullable(customer));

        List<String> resStr = authService.verifySecurityAnswer(securityAnswerDTO);
        verify(mockUserRepo, times(1)).findByUsername(securityAnswerDTO.getUsername());
        assertEquals("Incorrect Answer", resStr.get(0));
        assertEquals("1st attempt failed", resStr.get(1));
    }

    @Test
    @DisplayName("Test verifySecurityAnswer returns the second attempt failed message string")
    void testThatVerifySecurityAnswerReturnsSecondAttemptFailedMessageString() {
        customer.setSecurityAnswer("spaghetti");
        customer.setIncorrectAnswerCount(1);
        when(mockUserRepo.findByUsername(securityAnswerDTO.getUsername())).thenReturn(Optional.ofNullable(customer));

        List<String> resStr = authService.verifySecurityAnswer(securityAnswerDTO);
        verify(mockUserRepo, times(1)).findByUsername(securityAnswerDTO.getUsername());
        assertEquals("Incorrect Answer", resStr.get(0));
        assertEquals("2nd attempt failed", resStr.get(1));
    }

    @Test
    @DisplayName("Test verifySecurityAnswer returns the account lock message string")
    void testThatVerifySecurityAnswerReturnsAccountLockMessageString() {
        customer.setSecurityAnswer("spaghetti");
        customer.setIncorrectAnswerCount(2);
        when(mockUserRepo.findByUsername(securityAnswerDTO.getUsername())).thenReturn(Optional.ofNullable(customer));

        List<String> resStr = authService.verifySecurityAnswer(securityAnswerDTO);
        verify(mockUserRepo, times(1)).findByUsername(securityAnswerDTO.getUsername());
        assertEquals("Account Locked", resStr.get(0));
        assertEquals("too many failed attempts", resStr.get(1));
        assertEquals(true, customer.getAccountLockedFlag());
    }

    @Test
    @DisplayName("Test verifySecurityAnswer throws IllegalArgumentException")
    void testThatVerifySecurityAnswerThrowsIllegalArgumentException() {
        SecurityAnswerDTO securityAnswerDTO = new SecurityAnswerDTO();
        Exception exception = assertThrows(
                IllegalArgumentException.class, ()-> authService.verifySecurityAnswer(securityAnswerDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(String.format("User with username %s not found!", null)));
    }

    @Test
    @DisplayName("Test verifySecurityAnswer throws IllegalStateException")
    void testThatVerifySecurityAnswerThrowsIllegalStateException() {
        customer.setAccountLockedFlag(true);
        when(mockUserRepo.findByUsername(securityAnswerDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        Exception exception = assertThrows(
                IllegalStateException.class, ()-> authService.verifySecurityAnswer(securityAnswerDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Account is locked. Please contact our administration."));
    }

    @Test
    @DisplayName("Test verifySecurityAnswer throws IllegalStateException2")
    void testThatVerifySecurityAnswerThrowsIllegalStateException2() {
        customer.setTempLockOutExpiration(LocalDateTime.now().plusSeconds(1L));
        when(mockUserRepo.findByUsername(securityAnswerDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        Exception exception = assertThrows(
                IllegalStateException.class, ()-> authService.verifySecurityAnswer(securityAnswerDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(String.format(
                "Account is temporarily locked. Please wait till %s.", customer.getTempLockOutExpiration().toString()
        )));
    }

    @Test
    @DisplayName("Test verifySecurityAnswer throws NullPointerException1")
    void testThatVerifySecurityAnswerThrowsNullPointerException1() {
        customer.setEmail(null);
        when(mockUserRepo.findByUsername(securityAnswerDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        headers.set("User-Agent", "testDeviceInfo");
        Exception exception = assertThrows(
                NullPointerException.class, ()-> authService.verifySecurityAnswer(securityAnswerDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Email must not be null!"));
    }

    @Test
    @DisplayName("Test verifySecurityAnswer throws NullPointerException2")
    void testThatVerifySecurityAnswerThrowsNullPointerException2() {
        customer.setPhoneNumber(null);
        when(mockUserRepo.findByUsername(securityAnswerDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        headers.set("User-Agent", "testDeviceInfo");
        Exception exception = assertThrows(
                NullPointerException.class, ()-> authService.verifySecurityAnswer(securityAnswerDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Phone number must not be null!"));
    }

    @Test
    @DisplayName("Test requestSecurityCode returns a success message string for email option")
    void testThatRequestSecurityCodeReturnsSuccessMessageStringForEmailOption() {
        customer.setIsAnswerCorrect(true);
        when(mockUserRepo.findByUsername(securityCodeRequestDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        String resStr = authService.requestSecurityCode(securityCodeRequestDTO);
        verify(mockUserRepo, times(1)).findByUsername(securityCodeRequestDTO.getUsername());
        assertEquals("Security Code Sent Successfully", resStr);
    }

    @Test
    @DisplayName("Test requestSecurityCode returns a success message string for text option")
    void testThatRequestSecurityCodeReturnsSuccessMessageStringForTextOption() {
        customer.setIsAnswerCorrect(true);
        securityCodeRequestDTO = SecurityCodeRequestDTO.builder()
                .username(customer.getUsername())
                //.emailOrText("text")
                //.contactInfo("626-626-6266")
                .build();
        when(mockUserRepo.findByUsername(securityCodeRequestDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        String resStr = authService.requestSecurityCode(securityCodeRequestDTO);
        verify(mockUserRepo, times(1)).findByUsername(securityCodeRequestDTO.getUsername());
        assertEquals("Security Code Sent Successfully", resStr);
    }

    @Test
    @DisplayName("Test requestSecurityCode throws IllegalArgumentException")
    void testThatRequestSecurityCodeThrowsIllegalArgumentException() {
        SecurityCodeRequestDTO securityCodeRequestDTO = new SecurityCodeRequestDTO();
        Exception exception = assertThrows(
                IllegalArgumentException.class, ()-> authService.requestSecurityCode(securityCodeRequestDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(String.format("User with username %s not found!", null)));
    }

    @Test
    @DisplayName("Test requestSecurityCode throws IllegalArgumentException2")
    void testThatRequestSecurityCodeThrowsIllegalArgumentException2() {
        customer.setIsAnswerCorrect(true);
        securityCodeRequestDTO = SecurityCodeRequestDTO.builder()
                .username(customer.getUsername())
                //.emailOrText("text")
                //.contactInfo("")
                .build();
        when(mockUserRepo.findByUsername(securityCodeRequestDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        Exception exception = assertThrows(
                IllegalArgumentException.class, ()-> authService.requestSecurityCode(securityCodeRequestDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("The email address or phone number should not be null or empty!"));
    }

    @Test
    @DisplayName("Test requestSecurityCode throws IllegalArgumentException3")
    void testThatRequestSecurityCodeThrowsIllegalArgumentException3() {
        customer.setIsAnswerCorrect(true);
        securityCodeRequestDTO = SecurityCodeRequestDTO.builder()
                .username(customer.getUsername())
                //.emailOrText("email")
                //.contactInfo("incorrect@email.com")
                .build();
        when(mockUserRepo.findByUsername(securityCodeRequestDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        Exception exception = assertThrows(
                IllegalArgumentException.class, ()-> authService.requestSecurityCode(securityCodeRequestDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("The email addresses don't match!"));
    }

    @Test
    @DisplayName("Test requestSecurityCode throws IllegalArgumentException4")
    void testThatRequestSecurityCodeThrowsIllegalArgumentException4() {
        customer.setIsAnswerCorrect(true);
        securityCodeRequestDTO = SecurityCodeRequestDTO.builder()
                .username(customer.getUsername())
                //.emailOrText("text")
                //.contactInfo("123-456-7890")
                .build();
        when(mockUserRepo.findByUsername(securityCodeRequestDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        Exception exception = assertThrows(
                IllegalArgumentException.class, ()-> authService.requestSecurityCode(securityCodeRequestDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("The phone numbers don't match!"));
    }

    @Test
    @DisplayName("Test requestSecurityCode throws IllegalStateException")
    void testThatRequestSecurityCodeThrowsIllegalStateException() {
        customer.setIsAnswerCorrect(false);
        when(mockUserRepo.findByUsername(securityCodeRequestDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        Exception exception = assertThrows(
                IllegalStateException.class, ()-> authService.requestSecurityCode(securityCodeRequestDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("The device must be authorized or the security question must be answer once again!"));
    }

    @Test
    @DisplayName("Test requestSecurityCode throws IllegalArgumentException for invalid option")
    void testThatRequestSecurityCodeThrowsIllegalArgumentExceptionForInvalidOption() {
        customer.setIsAnswerCorrect(true);
        securityCodeRequestDTO = SecurityCodeRequestDTO.builder()
                .username(customer.getUsername())
                //.emailOrText("fax")
                //.contactInfo("123-456-7890")
                .build();
        when(mockUserRepo.findByUsername(securityCodeRequestDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        Exception exception = assertThrows(
                IllegalArgumentException.class, ()-> authService.requestSecurityCode(securityCodeRequestDTO)
        );
        String actualMessage = exception.getMessage();
        //assertTrue(actualMessage.contains(String.format("Unable to send the security code to %s!", securityCodeRequestDTO.getEmailOrText())));
    }

    @Test
    @DisplayName("Test that verifySecurityCode returns a success message string")
    void testThatVerifySecurityCodeReturnsSuccessMessageString() {
        when(mockUserRepo.findByUsername(securityCodeVerificationDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        String resStr = authService.verifySecurityCode(securityCodeVerificationDTO);
        verify(mockUserRepo, times(1)).findByUsername(securityCodeVerificationDTO.getUsername());
        assertEquals("Security Code Verified Successfully", resStr);
    }

    @Test
    @DisplayName("Test that verifySecurityCode returns a unsuccessful message string")
    void testThatVerifySecurityCodeReturnsUnsuccessfulMessageString() {
        customer.setSecurityCode(789);
        when(mockUserRepo.findByUsername(securityCodeVerificationDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        String resStr = authService.verifySecurityCode(securityCodeVerificationDTO);
        verify(mockUserRepo, times(1)).findByUsername(securityCodeVerificationDTO.getUsername());
        assertEquals("Security Code Incorrect", resStr);
    }

    @Test
    @DisplayName("Test that verifySecurityCode throws IllegalArgumentException")
    void testThatVerifySecurityCodeThrowsIllegalArgumentException() {
        SecurityCodeVerificationDTO securityCodeVerificationDTO = new SecurityCodeVerificationDTO();
        Exception exception = assertThrows(
                IllegalArgumentException.class, ()-> authService.verifySecurityCode(securityCodeVerificationDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(String.format("User with username %s not found!", null)));
    }

    @Test
    @DisplayName("Test updatePassword returns a success message string")
    void testThatUpdatePasswordReturnsSuccessMessageString() {
        customer.setIsSecurityCodeVerified(true);
        when(mockUserRepo.findByUsername(passwordUpdateDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        String resStr = authService.updatePassword(passwordUpdateDTO);
        verify(mockUserRepo, times(1)).findByUsername(passwordUpdateDTO.getUsername());
        assertEquals("Password Updated Successfully", resStr);
    }

    @Test
    @DisplayName("Test updatePassword throws IllegalArgumentException")
    void testThatUpdatePasswordThrowsIllegalArgumentException() {
        PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO();
        Exception exception = assertThrows(
                IllegalArgumentException.class, ()-> authService.updatePassword(passwordUpdateDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(String.format("User with username %s not found!", null)));
    }

    @Test
    @DisplayName("Test updatePassword throws IllegalStateException")
    void testThatUpdatePasswordThrowsIllegalStateException() {
        customer.setIsSecurityCodeVerified(false);
        when(mockUserRepo.findByUsername(passwordUpdateDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        Exception exception = assertThrows(
                IllegalStateException.class, ()-> authService.updatePassword(passwordUpdateDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(
                "Security code must be verified in order to update the password!"
        ));
    }

    @Test
    @DisplayName("Test updatePassword throws IllegalArgumentException for invalid password")
    void testThatUpdatePasswordThrowsIllegalArgumentExceptionForInvalidPassword() {
        customer.setIsSecurityCodeVerified(true);
        passwordUpdateDTO = PasswordUpdateDTO.builder()
                .username(customer.getUsername())
                .password("invalidPassword")
                .build();
        when(mockUserRepo.findByUsername(passwordUpdateDTO.getUsername())).thenReturn(Optional.ofNullable(customer));
        Exception exception = assertThrows(
                IllegalArgumentException.class, ()-> authService.updatePassword(passwordUpdateDTO)
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(PASSWORD_ERROR_MESSAGE));
    }

}
