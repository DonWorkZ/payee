package com.fdmgroup.pilotbank.authentication.security;

import com.fdmgroup.pilotbank2.authentication.security.JWTAuthenticationFilter;
import com.fdmgroup.pilotbank2.authentication.security.MyUserDetails;
import com.fdmgroup.pilotbank2.authentication.security.UserDetailsServiceImpl;
import com.fdmgroup.pilotbank2.authentication.security.UserPrincipal;
import com.fdmgroup.pilotbank2.models.Address;
import com.fdmgroup.pilotbank2.models.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.fdmgroup.pilotbank2.authentication.security.SecurityConstants.HEADER_STRING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class JWTAuthenticationFilterTest {

    JWTAuthenticationFilter jwtAuthenticationFilter;
    ExtendedJWTAuthenticationFilter extendedJWTAuthenticationFilter;

    @Mock
    private UserDetailsServiceImpl mockUserDetailsService;

    @Mock
    private AuthenticationManager mockAuthenticationManager;

    private AuthenticationManager authenticationManager;

    private MockHttpServletRequest mockedRequest = new MockHttpServletRequest();
    private MockHttpServletResponse mockedResponse = new MockHttpServletResponse();

    private FilterChain chain;
    private Address address;
    private Customer customer;
    private MyUserDetails myUserDetails;
    private List<GrantedAuthority> authorities;
    private UserPrincipal principal;
    private Authentication authentication;

    @BeforeEach
    void init() {
        authenticationManager = new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return null;
            }
        };

        chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

            }
        };

        address = Address.builder()
                .streetNumber("123").streetName("Wall Street").suiteNumber(null)
                .city("New York").province("NY").postalCode("11111").build();

        customer = Customer.builder()
                .userId(1111L)
                .addressList(Arrays.asList(address))
                .username("test4")
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

        authorities = Arrays.stream(customer.getRole().split(",")).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        authorities = Arrays.stream(customer.getRole().split(",")).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        myUserDetails = new MyUserDetails() {
            @Override
            public Long getId() {
                return customer.getUserId();
            }

            @Override
            public String getFirstName() {
                return customer.getFirstName();
            }

            @Override
            public String getLastName() {
                return customer.getLastName();
            }

            @Override
            public String getEmail() {
                return customer.getEmail();
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return authorities;
            }

            @Override
            public String getPassword() {
                return customer.getPassword();
            }

            @Override
            public String getUsername() {
                return customer.getUsername();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };

        principal = new UserPrincipal(customer);

        authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return authorities;
            }

            @Override
            public Object getCredentials() {
                return customer.getPassword();
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
                return customer.getUsername();
            }
        };

        jwtAuthenticationFilter = new JWTAuthenticationFilter(mockAuthenticationManager, mockUserDetailsService);
        extendedJWTAuthenticationFilter = new ExtendedJWTAuthenticationFilter(mockAuthenticationManager, mockUserDetailsService);
    }

    @Test
    @DisplayName("Test that attemptAuthentication returns authentication")
    void test_attemptAuthentication_returns_authentication() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String json = "{\"username\": \"test4\", \"password\": \"1A2b3c4!\"}";
        when(request.getInputStream()).thenReturn(
                new DelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));

        when(mockAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                "test4",
                "1A2b3c4!",
                new ArrayList<>()
        ))).thenReturn(new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
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
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        });

        Authentication authentication = jwtAuthenticationFilter.attemptAuthentication(request, mockedResponse);
        assertNotNull(authentication);
        assertTrue(authentication instanceof Authentication);
    }

    @Test
    @DisplayName("Test that attemptAuthentication throws RuntimeException")
    void test_attemptAuthentication_returns_RuntimeException() throws IOException {
        assertThrows(RuntimeException.class, () -> {jwtAuthenticationFilter.attemptAuthentication(mockedRequest, mockedResponse);});
    }

    @Test
    @DisplayName("Test that successfulAuthentication adds header to response")
    void test_successfulAuthentication_adds_header_to_response() throws IOException, ServletException {
        when(mockUserDetailsService.loadUserByUsername("test4")).thenReturn(principal);
        extendedJWTAuthenticationFilter.successfulAuthentication(mockedRequest, mockedResponse, chain, authentication);
        assertTrue(mockedResponse.getHeader(HEADER_STRING).contains("Bearer ey"));
    }

}
