package com.fdmgroup.pilotbank.authentication.security;

import com.fdmgroup.pilotbank2.authentication.security.MyUserDetails;
import com.fdmgroup.pilotbank2.authentication.security.UserDetailsServiceImpl;
import com.fdmgroup.pilotbank2.authentication.security.UserPrincipal;
import com.fdmgroup.pilotbank2.models.Address;
import com.fdmgroup.pilotbank2.models.Checking;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class UserDetailsServiceImplTest {

    @Mock
    UserRepo mockUserRepo;

    Address address;
    Customer customer;
    Checking checking;

    @BeforeEach
    void init() {
        address = Address.builder()
                .streetNumber("123").streetName("Wall Street").suiteNumber(null)
                .city("New York").province("NY").postalCode("11111").build();

        customer = Customer.builder()
                .userId(1111L)
                .addressList(Arrays.asList(address))
                .username("testUsername")
                .firstName("testFirst")
                .lastName("testLast")
                .email("test@email.com")
                .phoneNumber("123-456-7890")
                .password("testPassword")
                .role("CUSTOMER")
                .isActive(true)
                .sin("963-852-7410")
                .occupation("Pilot")
                .industry("Aviation")
                .title("Mr.")
                .income(BigDecimal.valueOf(80000))
                .build();

        checking = Checking.builder()
                .accountId(1111L)
                .accountType(AccountTypeEnum.CHECKING)
                .ownedAccountCustomer(customer)
                .isMainAccount(true)
                .monthlyTransactionsRemaining(Checking.monthlyTransactionAmount)
                .build();

        customer.setMainAccount(checking);
        customer.setOwnedAccounts(Arrays.asList(checking));
    }

    @Test
    @DisplayName("Test that the all arg constructor returns an instance")
    void test_that_all_arg_constructor_returns_instance() {
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(mockUserRepo);
        assertTrue(userDetailsService instanceof UserDetailsServiceImpl);
    }

    @Test
    @DisplayName("Test that loadUserByUsername returns UserPrincipal")
    void test_that_loadUserByUsername_returns_UserPrincipal() {
        when(mockUserRepo.findByUsername("testUsername")).thenReturn(Optional.of(customer));

        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(mockUserRepo);
        MyUserDetails myUserDetails = (MyUserDetails) userDetailsService.loadUserByUsername("testUsername");
        assertEquals("testFirst", myUserDetails.getFirstName());
        assertEquals("testLast", myUserDetails.getLastName());
        assertEquals("test@email.com", myUserDetails.getEmail());
    }

    @Test
    @DisplayName("test that loadUserByUsername throws a UsernameNotFoundException")
    void test_that_loadUserByUsername_throws_UsernameNotFoundException(){
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(mockUserRepo);
        Exception exception = assertThrows(UsernameNotFoundException.class, ()->userDetailsService.loadUserByUsername("invalidUser"));
        String expectedMessage = String.format("User with username: %s not found!", "invalidUser");
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("test that convertGrantedAuthoritiesToStrings returns a list of String")
    void test_that_convertGrantedAuthoritiesToStrings_returns_list_of_String(){
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(mockUserRepo);
        GrantedAuthority auth = new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "CUSTOMER";
            }
        };
        List<GrantedAuthority> authList = Arrays.asList(auth);
        List<String> strings = userDetailsService.convertGrantedAuthoritiesToStrings(authList);
        assertNotNull(strings);
    }
}
