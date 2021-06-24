package com.fdmgroup.pilotbank.authentication.security;

import com.fdmgroup.pilotbank2.authentication.security.UserPrincipal;
import com.fdmgroup.pilotbank2.models.Account;
import com.fdmgroup.pilotbank2.models.Address;
import com.fdmgroup.pilotbank2.models.Checking;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class})
public class UserPrincipalTest {

    Customer customer;
    Checking checking;
    Address address;

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
    @DisplayName("Test that no arg constructor returns an instance with default values")
    void test_that_no_arg_constructor_returns_instance_with_default_values() {
        UserPrincipal userPrincipal = new UserPrincipal();
        assertEquals(null, userPrincipal.getUsername());
        assertEquals(null, userPrincipal.getPassword());
        assertEquals(null, userPrincipal.getFirstName());
        assertEquals(null, userPrincipal.getLastName());
        assertEquals(null, userPrincipal.getEmail());
        assertEquals(null, userPrincipal.getId());
    }

    @Test
    @DisplayName("Test that all arg constructor returns an instance with specified values")
    void test_that_all_arg_constructor_returns_instance_with_specified_values() {
        UserPrincipal userPrincipal = new UserPrincipal(customer);
        assertEquals("testUsername", userPrincipal.getUsername());
        assertEquals("testPassword", userPrincipal.getPassword());
        assertEquals("testFirst", userPrincipal.getFirstName());
        assertEquals("testLast", userPrincipal.getLastName());
        assertEquals("test@email.com", userPrincipal.getEmail());
        assertEquals(1111L, userPrincipal.getId());
    }

    @Test
    @DisplayName("Test isAccountNonExpired returns true")
    void test_isAccountNonExpired_returns_true() {
        UserPrincipal userPrincipal = new UserPrincipal(customer);
        boolean expired = userPrincipal.isAccountNonExpired();
        assertTrue(expired);

        customer.setAccountExpires(LocalDateTime.now().plusDays(90L));
        userPrincipal = new UserPrincipal(customer);
        expired = userPrincipal.isAccountNonExpired();
        assertTrue(expired);
    }

    @Test
    @DisplayName("Test isAccountNonLocked returns true")
    void test_isAccountNonLocked_returns_true() {
        UserPrincipal userPrincipal = new UserPrincipal(customer);
        boolean locked = userPrincipal.isAccountNonLocked();
        assertTrue(locked);
    }

    @Test
    @DisplayName("Test isCredentialsNonExpired returns true")
    void test_isCredentialsNonExpired_returns_true() {
        UserPrincipal userPrincipal = new UserPrincipal(customer);
        boolean expired = userPrincipal.isCredentialsNonExpired();
        assertTrue(expired);
    }

    @Test
    @DisplayName("Test isisEnabled returns true")
    void test_isEnabled_returns_true() {
        UserPrincipal userPrincipal = new UserPrincipal(customer);
        boolean enabled = userPrincipal.isEnabled();
        assertTrue(enabled);
    }

    @Test
    @DisplayName("Test that setAuthorities updates a list of GrantedAuthority")
    void test_that_setAuthorities_updates_list_of_GrantedAuthority() {
        UserPrincipal userPrincipal = new UserPrincipal(customer);
        customer.setRole("ADMIN");
        userPrincipal = new UserPrincipal(customer);
        userPrincipal.setAuthorities();
        assertNotEquals(Arrays.asList("CUSTOMER"), userPrincipal.getAuthorities());
    }
}
