package com.fdmgroup.pilotbank.controllers;

import com.fdmgroup.pilotbank2.authentication.AuthenticationRequest;
import com.fdmgroup.pilotbank2.authentication.security.MyUserDetails;
import com.fdmgroup.pilotbank2.authentication.security.UserDetailsServiceImpl;
import com.fdmgroup.pilotbank2.authentication.security.UserPrincipal;
import com.fdmgroup.pilotbank2.controllers.UserController;
import com.fdmgroup.pilotbank2.models.Address;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.User;
import com.fdmgroup.pilotbank2.models.dto.*;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import com.fdmgroup.pilotbank2.services.UserService;
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
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepo mockUserRepo;

    @Mock
    private UserService mockUserService;

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
    @DisplayName("Test that getUsers returns a list of users")
    void test_getUsers_returns_userList() {
        when(mockUserService.findAll()).thenReturn(Arrays.asList(customer1));
        ResponseEntity<List<User>> users = userController.getUsers();
        verify(mockUserService, times(1)).findAll();
        assertEquals(1, users.getBody().size());
        assertEquals(customer1, users.getBody().get(0));
        assertEquals(HttpStatus.OK, users.getStatusCode());
    }

    @Test
    @DisplayName("Test that getUserById returns a user")
    void test_getUserById_returns_user() {
        when(mockUserService.findById(customer2.getUserId())).thenReturn(customer2);
        User user = userController.getUserById(customer2.getUserId());
        verify(mockUserService, times(1)).findById(customer2.getUserId());
        assertEquals(customer2, user);
    }

    @Test
    @DisplayName("Test that updateUser returns an updated user")
    void test_updateUser_returns_updatedUser() {
        customer1.setTitle("Mrs.");
        customer1.setEmail("updatedUser@email.com");
        customer1.setPhoneNumber("000-000-0000");
        customer1.setPassword("newPassword");
        customer1.setOccupation("Doctor");
        customer1.setIndustry("Healthcare");
        customer1.setAddressList(Arrays.asList(address2));

        when(mockUserService.updateUser(customer1.getUserId(), userUpdateDTO)).thenReturn(customer1);
        ResponseEntity<?> user = userController.updateUser(customer1.getUserId(), userUpdateDTO);
        verify(mockUserService, times(1)).updateUser(customer1.getUserId(), userUpdateDTO);
        assertEquals(customer1, user.getBody());
        assertEquals(HttpStatus.OK, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that updateUser throws exception")
    void test_updateUser_throws_Exception() {
        when(mockUserService.updateUser(1010L, userUpdateDTO))
                .thenThrow(new IllegalArgumentException("User does not exist.") {});
        ResponseEntity<?> user = userController.updateUser(1010L, userUpdateDTO);

        String expectedMessage = String.format("Error occurred while updating User with ID: %s, Error: %s",
                1010L, "com.fdmgroup.pilotbank.controllers.UserControllerTest$2: User does not exist.");
        assertEquals(expectedMessage, user.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that createCustomer returns a new customer")
    void test_createCustomer_returns_newCustomer() throws Exception {
        when(mockUserService.createCustomer(customerCreationDTO)).thenReturn(customer3);
        ResponseEntity<?> customer = userController.createCustomer(customerCreationDTO);
        verify(mockUserService, times(1)).createCustomer(customerCreationDTO);
        assertEquals(customer3, customer.getBody());
        assertEquals(HttpStatus.CREATED, customer.getStatusCode());
    }

    @Test
    @DisplayName("Test that createCustomer throws exception")
    void test_createCustomer_throws_Exception() throws Exception {
        when(mockUserService.createCustomer(customerCreationDTO))
                .thenThrow(new IllegalArgumentException("IllegalArgumentException") {});
        ResponseEntity<?> customer = userController.createCustomer(customerCreationDTO);

        String expectedMessage = String.format("Error creating customer: %s",
                "com.fdmgroup.pilotbank.controllers.UserControllerTest$3: IllegalArgumentException");
        assertEquals(expectedMessage, customer.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, customer.getStatusCode());
    }

    @Test
    @DisplayName("Test that deleteUser returns a user")
    void test_deleteUser_returns_user() throws Exception {
        when(mockUserService.deleteUser(customer2.getUserId())).thenReturn(customer2);
        ResponseEntity<?> user = userController.deleteUser(customer2.getUserId());
        verify(mockUserService, times(1)).deleteUser(customer2.getUserId());
        assertEquals(customer2, user.getBody());
        assertEquals(HttpStatus.OK, user.getStatusCode());
    }

    @Test
    @DisplayName("Test that deleteUser throws exception")
    void test_deleteUser_throws_Exception() throws Exception {
        when(mockUserService.deleteUser(customer2.getUserId()))
                .thenThrow(new IllegalArgumentException("IllegalArgumentException") {});
        ResponseEntity<?> user = userController.deleteUser(customer2.getUserId());

        String expectedMessage = String.format("Error deleting customer: %s",
                "com.fdmgroup.pilotbank.controllers.UserControllerTest$4: IllegalArgumentException");
        assertEquals(expectedMessage, user.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, user.getStatusCode());
    }

}
