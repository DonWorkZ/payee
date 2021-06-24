package com.fdmgroup.pilotbank.controllers;

import com.fdmgroup.pilotbank2.authentication.AuthenticationRequest;
import com.fdmgroup.pilotbank2.authentication.security.MyUserDetails;
import com.fdmgroup.pilotbank2.authentication.security.UserDetailsServiceImpl;
import com.fdmgroup.pilotbank2.authentication.security.UserPrincipal;
import com.fdmgroup.pilotbank2.controllers.AuthController;
import com.fdmgroup.pilotbank2.models.Address;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.dto.*;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import com.fdmgroup.pilotbank2.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserRepo mockUserRepo;

    @Mock
   private AuthService mockAuthService;

    @Mock
    private AuthenticationManager mockAuthenticationManager;

    @Mock
    private UserDetailsServiceImpl mockUserDetailsService;

    private Address address1, address2, address3;
    private Customer customer1, customer2, customer3;
    private List<Customer> customers = new ArrayList<>();
    private UserUpdateDTO userUpdateDTO;
    private UserPrincipal principal;
    private List<GrantedAuthority> authorities;
    private Authentication userAuth;
    private AuthenticationRequest authRequest;
    private CustomerCreationDTO customerCreationDTO = new CustomerCreationDTO();
    private AccountCreationDTO accountCreationDTO = new AccountCreationDTO();
    private UsernameDTO usernameDTO = new UsernameDTO();
    private SecurityAnswerDTO securityAnswerDTO = new SecurityAnswerDTO();
    private SecurityCodeRequestDTO securityCodeRequestDTO = new SecurityCodeRequestDTO();
    private SecurityCodeVerificationDTO securityCodeVerificationDTO = new SecurityCodeVerificationDTO();
    private PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO();
    private HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    void init() {
        address1 = Address.builder()
                .streetNumber("123").streetName("Wall Street").suiteNumber(null)
                .city("New York").province("NY").postalCode("11111").build();

        address2 = Address.builder()
                .streetNumber("147").streetName("5th Ave").suiteNumber("101")
                .city("San Francisco").province("CA").postalCode("90000").build();

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

        customer2 = Customer.builder()
                .userId(888L).title("Ms.").firstName("Pilot").lastName("Bank")
                .username("Experiment888")
                .email("experiment888@email.com")
                .phoneNumber("626-626-7841")
                .sin("{bcrypt}$2a$10$mWkWlyaB89vtZPwRk0Gb1u3GZK08mDDc04tL/2Air0RFz.j0.2MR6")
                .password("{bcrypt}$2a$10$flvQjNBf1Fwd3ETm6Ttf8eVKPhvSzCGGj5xYBKOLTqRtwdmqwlGDu")
                .passwordExpires(LocalDateTime.of(2021, 02, 12, 00, 00, 00, 00)
                        .plusDays(90L))
                .role("CUSTOMER").isActive(true).lastFailedLoginDate(null).accountLockedFlag(false)
                .addressList(Arrays.asList(address2))
                .accountExpires(null).industry("Energy").occupation("Engineer").build();

        customers.add(customer1);
        customers.add(customer2);

        userUpdateDTO = UserUpdateDTO.builder()
                .title("Mrs.")
                .email("updatedUser@email.com")
                .phoneNumber("000-000-0000")
                .password("newPassword")
                .occupation("Doctor")
                .industry("Healthcare")
                .address(address2)
                .build();

        principal = new UserPrincipal(customer1);

        authorities = Arrays.stream(customer1.getRole().split(",")).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        userAuth = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return authorities;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return principal;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }

            @Override
            public String getName() {
                return "Experiment999";
            }
        };

        userAuth.setAuthenticated(true);

        authRequest = new AuthenticationRequest(customer1.getUsername(), customer1.getPassword());

        address3 = Address.builder()
                .streetNumber("228").streetName("2nd Road")
                .city("Chicago").province("IL").postalCode("60000").build();

        accountCreationDTO = AccountCreationDTO.builder()
                .balance(BigDecimal.valueOf(100L))
                .accountType("SAVINGS")
                .isMainAccount(true)
                .build();

        customerCreationDTO = CustomerCreationDTO.builder()
                .username("Experiment600")
                .firstName("Angular")
                .lastName("Spring")
                .email("experiment600@email.com")
                .phoneNumber("626-626-6363")
                .password("{bcrypt}$2a$10$flvQjNBf1Fwd3ETm6Ttf8eVKPhvSzCGGj5xYBKOLTqRtwdmqwlGDu")
                .role("CUSTOMER")
                .isActive(true)
                .sin("{bcrypt}$2a$10$mWkWlyaB89vtZPwRk0Gb1u3GZK08mDDc04tL/2Air0RFz.j0.2MR6")
                .occupation("Teacher")
                .industry("Education")
                .income(BigDecimal.valueOf(60000L))
                .title("Mrs.")
                .address(address3)
                .account(accountCreationDTO)
                .build();

        customer3 = Customer.builder()
                .userId(666L).title("Mrs.").firstName("Angular").lastName("Spring")
                .username("Experiment600")
                .email("experiment600@email.com")
                .phoneNumber("626-626-6363")
                .sin("{bcrypt}$2a$10$mWkWlyaB89vtZPwRk0Gb1u3GZK08mDDc04tL/2Air0RFz.j0.2MR6")
                .password("{bcrypt}$2a$10$flvQjNBf1Fwd3ETm6Ttf8eVKPhvSzCGGj5xYBKOLTqRtwdmqwlGDu")
                .passwordExpires(LocalDateTime.of(2021, 02, 16, 00, 00, 00, 00)
                        .plusDays(90L))
                .role("CUSTOMER").isActive(true).lastFailedLoginDate(null).accountLockedFlag(false)
                .addressList(Arrays.asList(address3))
                .accountExpires(null).industry("Education").occupation("Teacher").build();

        usernameDTO = UsernameDTO.builder()
                .username(customer1.getUsername())
                .build();

        securityAnswerDTO = SecurityAnswerDTO.builder()
                .username(customer1.getUsername())
                .answer(customer1.getSecurityAnswer())
                .build();

        securityCodeRequestDTO = SecurityCodeRequestDTO.builder()
                .username(customer1.getUsername())
                //.emailOrText("email")
                .build();

        securityCodeVerificationDTO = SecurityCodeVerificationDTO.builder()
                .username(customer1.getUsername())
                .securityCode("testCode1234")
                .build();

        passwordUpdateDTO = PasswordUpdateDTO.builder()
                .username(customer1.getUsername())
                .password("newPW123!")
                .build();
    }

    @Test
    @DisplayName("Test that login returns a token")
    void test_login_returns_token() throws Exception {
        customer1.setTempLockOutExpiration(LocalDateTime.parse("1970-01-01T00:00:00"));
        when(mockUserRepo.findByUsername(authRequest.getUsername())).thenReturn(Optional.ofNullable(customer1));
        when(mockAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()))
        ).thenReturn(userAuth);

        GrantedAuthority auth = new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "CUSTOMER";
            }
        };

        List<GrantedAuthority> authList = Arrays.asList(auth);

        when(mockUserDetailsService.loadUserByUsername(((UserPrincipal) userAuth.getPrincipal()).getUsername()))
                .thenReturn((MyUserDetails) principal);

        List<String> authoritiesList = Arrays.asList("CUSTOMER");

        lenient().when(mockUserDetailsService.convertGrantedAuthoritiesToStrings(authList)).thenReturn(authoritiesList);

        ResponseEntity<?> res = authController.login(authRequest, headers);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertTrue(res.getBody().toString().contains("Bearer ey"));

        res = authController.login(authRequest, null);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertTrue(res.getBody().toString().contains("Bearer ey"));

        headers.set("User-Agent", "testDevice");
        res = authController.login(authRequest, headers);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertTrue(res.getBody().toString().contains("Bearer ey"));
    }

    @Test
    @DisplayName("Test that login throws IllegalArgumentException")
    void test_login_throws_IllegalArgumentException() throws Exception {
        when(mockUserRepo.findByUsername("Experiment999"))
                .thenThrow(new IllegalArgumentException(String.format("User with username %s not found!", "Experiment999")));

        ResponseEntity<?> res = authController.login(authRequest, headers);

        String expectedMessage = String.format(
                "Unable to login: java.lang.IllegalArgumentException: User with username %s not found!", "Experiment999");
        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
        assertEquals(expectedMessage, res.getBody());
    }

    @Test
    @DisplayName("Test that login returns the account lock message")
    void test_login_returns_account_lock_message() throws Exception {
        customer1.setAccountLockedFlag(true);
        when(mockUserRepo.findByUsername(authRequest.getUsername())).thenReturn(Optional.ofNullable(customer1));

        ResponseEntity<?> res = authController.login(authRequest, headers);

        String expectedMessage = String.format("Account is locked. Please contact our administration.");
        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
        assertEquals(expectedMessage, res.getBody());
    }

    @Test
    @DisplayName("Test that login returns the account temporary lock message")
    void test_login_returns_account_temporary_lock_message() throws Exception {
        customer1.setTempLockOutExpiration(LocalDateTime.now().plusSeconds(1L));
        when(mockUserRepo.findByUsername(authRequest.getUsername())).thenReturn(Optional.ofNullable(customer1));

        ResponseEntity<?> res = authController.login(authRequest, headers);

        String expectedMessage = String.format("Account is temporarily locked. Please wait till %s.",
                customer1.getTempLockOutExpiration().toString());
        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
        assertEquals(expectedMessage, res.getBody());
    }

    @Test
    @DisplayName("Test that login throws exception")
    void test_login_throws_Exception() throws Exception {
        customer1.setTempLockOutExpiration(LocalDateTime.parse("1970-01-01T00:00:00"));
        when(mockUserRepo.findByUsername(authRequest.getUsername())).thenReturn(Optional.ofNullable(customer1));
        when(mockAuthenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()))
        ).thenThrow(new BadCredentialsException("Bad Credential") {});

        ResponseEntity<?> res = authController.login(authRequest, headers);

        String expectedMessage = String.format("Unable to login: %s",
                "com.fdmgroup.pilotbank.controllers.AuthControllerTest$3: Bad Credential");
        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
        assertEquals(expectedMessage, res.getBody());
    }


    @Test
    @DisplayName("Test that authorizeDevice returns a response message string array")
    void test_authorizeDevice_returns_success_message_string_array() throws Exception {
        List<String> resArr = new ArrayList<>();
        resArr.add("Device Authorization Success");
        resArr.add("a***@***.com");
        resArr.add("01********9");

        when(mockAuthService.authorizeDevice(usernameDTO, headers))
                .thenReturn(resArr);
        ResponseEntity<?> user = authController.authorizeDevice(usernameDTO, headers);
        verify(mockAuthService, times(1)).authorizeDevice(usernameDTO, headers);
        assertEquals(resArr, user.getBody());
        assertEquals(HttpStatus.OK, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that authorizeDevice throws exception")
    void test_authorizeDevice_throws_Exception() throws Exception {
        when(mockAuthService.authorizeDevice(usernameDTO, headers))
                .thenThrow(new IllegalArgumentException("IllegalArgumentException") {});
        ResponseEntity<?> user = authController.authorizeDevice(usernameDTO, headers);

        String expectedMessage = String.format("Error authorizing the device: %s",
                "com.fdmgroup.pilotbank.controllers.AuthControllerTest$4: IllegalArgumentException");
        assertEquals(expectedMessage, user.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that fetchSecurityQuestion returns a security question")
    void test_fetchSecurityQuestion_returns_security_question() throws Exception {
        when(mockAuthService.fetchSecurityQuestion(usernameDTO)).thenReturn(customer1.getSecurityQuestion());
        ResponseEntity<?> user = authController.fetchSecurityQuestion(usernameDTO);
        verify(mockAuthService, times(1)).fetchSecurityQuestion(usernameDTO);
        assertEquals(customer1.getSecurityQuestion(), user.getBody());
        assertEquals(HttpStatus.OK, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that fetchSecurityQuestion throws exception")
    void test_fetchSecurityQuestion_throws_Exception() throws Exception {
        when(mockAuthService.fetchSecurityQuestion(usernameDTO))
                .thenThrow(new IllegalArgumentException("IllegalArgumentException") {});
        ResponseEntity<?> user = authController.fetchSecurityQuestion(usernameDTO);

        String expectedMessage = String.format("Error fetching the security question: %s",
                "com.fdmgroup.pilotbank.controllers.AuthControllerTest$5: IllegalArgumentException");
        assertEquals(expectedMessage, user.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that verifySecurityAnswer returns a success message")
    void test_verifySecurityAnswer_returns_success_message() throws Exception {
        List<String> resArr = new ArrayList<>();
        resArr.add("Correct Answer");
        resArr.add("a***@***.com");
        resArr.add("01********9");

        when(mockAuthService.verifySecurityAnswer(securityAnswerDTO)).thenReturn(resArr);
        ResponseEntity<?> user = authController.verifySecurityAnswer(securityAnswerDTO);
        verify(mockAuthService, times(1)).verifySecurityAnswer(securityAnswerDTO);
        assertEquals(resArr, user.getBody());
        assertEquals(HttpStatus.OK, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that verifySecurityAnswer throws exception")
    void test_verifySecurityAnswer_throws_Exception() throws Exception {
        when(mockAuthService.verifySecurityAnswer(securityAnswerDTO))
                .thenThrow(new IllegalArgumentException("IllegalArgumentException") {});
        ResponseEntity<?> user = authController.verifySecurityAnswer(securityAnswerDTO);

        String expectedMessage = String.format("Error verifying the security answer: %s",
                "com.fdmgroup.pilotbank.controllers.AuthControllerTest$6: IllegalArgumentException");
        assertEquals(expectedMessage, user.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that requestSecurityCode returns a success message")
    void test_requestSecurityCode_returns_success_message() throws Exception {
        when(mockAuthService.requestSecurityCode(securityCodeRequestDTO)).thenReturn("Security Code Sent Successfully");
        ResponseEntity<?> user = authController.requestSecurityCode(securityCodeRequestDTO);
        verify(mockAuthService, times(1)).requestSecurityCode(securityCodeRequestDTO);
        assertEquals("Security Code Sent Successfully", user.getBody());
        assertEquals(HttpStatus.OK, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that requestSecurityCode throws exception")
    void test_requestSecurityCode_throws_Exception() throws Exception {
        when(mockAuthService.requestSecurityCode(securityCodeRequestDTO))
                .thenThrow(new IllegalArgumentException("IllegalArgumentException") {});
        ResponseEntity<?> user = authController.requestSecurityCode(securityCodeRequestDTO);

        String expectedMessage = String.format("Error requesting the security code: %s",
                "com.fdmgroup.pilotbank.controllers.AuthControllerTest$7: IllegalArgumentException");
        assertEquals(expectedMessage, user.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that verifySecurityCode returns a success message")
    void test_verifySecurityCode_returns_success_message() throws Exception {
        when(mockAuthService.verifySecurityCode(securityCodeVerificationDTO)).thenReturn("Security Code Verified Successfully");
        ResponseEntity<?> user = authController.verifySecurityCode(securityCodeVerificationDTO);
        verify(mockAuthService, times(1)).verifySecurityCode(securityCodeVerificationDTO);
        assertEquals("Security Code Verified Successfully", user.getBody());
        assertEquals(HttpStatus.OK, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that verifySecurityCode throws exception")
    void test_verifySecurityCode_throws_Exception() throws Exception {
        when(mockAuthService.verifySecurityCode(securityCodeVerificationDTO))
                .thenThrow(new IllegalArgumentException("IllegalArgumentException") {});
        ResponseEntity<?> user = authController.verifySecurityCode(securityCodeVerificationDTO);

        String expectedMessage = String.format("Error verifying the security code: %s",
                "com.fdmgroup.pilotbank.controllers.AuthControllerTest$8: IllegalArgumentException");
        assertEquals(expectedMessage, user.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that updatePassword returns a success message")
    void test_updatePassword_returns_success_message() throws Exception {
        when(mockAuthService.updatePassword(passwordUpdateDTO)).thenReturn("Password Updated Successfully");
        ResponseEntity<?> user = authController.updatePassword(passwordUpdateDTO);
        verify(mockAuthService, times(1)).updatePassword(passwordUpdateDTO);
        assertEquals("Password Updated Successfully", user.getBody());
        assertEquals(HttpStatus.OK, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that updatePassword throws exception")
    void test_updatePassword_throws_Exception() throws Exception {
        when(mockAuthService.updatePassword(passwordUpdateDTO))
                .thenThrow(new IllegalArgumentException("IllegalArgumentException") {});
        ResponseEntity<?> user = authController.updatePassword(passwordUpdateDTO);

        String expectedMessage = String.format("Error updating the password: %s",
                "com.fdmgroup.pilotbank.controllers.AuthControllerTest$9: IllegalArgumentException");
        assertEquals(expectedMessage, user.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, user.getStatusCode());
    }

}
