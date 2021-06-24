package com.fdmgroup.pilotbank.authentication.security;

import com.fdmgroup.pilotbank2.authentication.security.AuthenticationEventListener;
import com.fdmgroup.pilotbank2.authentication.security.UserPrincipal;
import com.fdmgroup.pilotbank2.models.Address;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith({MockitoExtension.class})
public class AuthenticationEventListenerTest {

    @InjectMocks
    private AuthenticationEventListener authenticationEventListener;

    @Mock
    private UserRepo mockUserRepo;

    @Mock
    private AuthenticationFailureBadCredentialsEvent mockBadEvent;

    @Mock
    private AuthenticationSuccessEvent mockGoodEvent;

    private List<GrantedAuthority> authorities;
    private Authentication authentication, authentication2;
    private Customer customer;
    private UserPrincipal principal;

    @BeforeEach
    void init() {
        customer = Customer.builder()
                .userId(1111L)
                .addressList(new ArrayList<>())
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
                .role("CUSTOMER").isActive(true).lastFailedLoginDate(null).accountLockedFlag(false)
                .build();

        principal = new UserPrincipal(customer);

        authorities = Arrays.stream(customer.getRole().split(",")).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        authentication = new Authentication() {
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
                return principal.getUsername();
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
                return "testUsername";
            }
        };

        authentication2 = new Authentication() {
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
                return "testUsername";
            }
        };
    }

    @Test
    @DisplayName("Test that authenticationFailed calls UserRepo save method")
    void test_that_authenticationFailed_calls_UserRepo_save_method() {
        when(mockBadEvent.getAuthentication()).thenReturn(authentication);
        when(mockUserRepo.findByUsername(customer.getUsername())).thenReturn(Optional.of(customer));

        authenticationEventListener.authenticationFailed(mockBadEvent);

        verify(mockUserRepo, times(1)).findByUsername("testUsername");
        verify(mockUserRepo, times(1)).save(customer);
    }

    @Test
    @DisplayName("Test that authenticationFailed throws IllegalStateException")
    void test_that_authenticationFailed_throws_IllegalStateException() {
        when(mockBadEvent.getAuthentication()).thenReturn(authentication);
        Exception exception = assertThrows(IllegalStateException.class, ()->authenticationEventListener.authenticationFailed(mockBadEvent));
        String expectedMessage = String.format("User with username %s not found", "testUsername");
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Test that authenticationSucceeded calls UserRepo save method")
    void test_that_authenticationSucceeded_calls_UserRepo_save_method() {

        when(mockGoodEvent.getAuthentication()).thenReturn(authentication2);
        when(mockUserRepo.findByUsername(customer.getUsername())).thenReturn(Optional.of(customer));

        authenticationEventListener.authenticationSucceeded(mockGoodEvent);

        verify(mockUserRepo, times(1)).findByUsername("testUsername");
        verify(mockUserRepo, times(1)).save(customer);
    }

    @Test
    @DisplayName("Test that authenticationSucceeded throws IllegalStateException")
    void test_that_authenticationSucceeded_throws_IllegalStateException() {
        when(mockGoodEvent.getAuthentication()).thenReturn(authentication2);
        Exception exception = assertThrows(IllegalStateException.class, ()->authenticationEventListener.authenticationSucceeded(mockGoodEvent));
        String expectedMessage = String.format("User with username %s not found", "testUsername");
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}
