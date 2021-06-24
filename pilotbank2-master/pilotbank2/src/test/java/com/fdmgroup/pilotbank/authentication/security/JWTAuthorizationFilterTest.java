package com.fdmgroup.pilotbank.authentication.security;

import com.fdmgroup.pilotbank2.authentication.security.JWTAuthorizationFilter;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.fdmgroup.pilotbank2.authentication.security.SecurityConstants.HEADER_STRING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class JWTAuthorizationFilterTest {

    private JWTAuthorizationFilter jwtAuthorizationFilter;

    @Mock
    private UserDetailsServiceImpl mockUserDetailsService;

    private MockHttpServletRequest mockedRequest = new MockHttpServletRequest();
    private MockHttpServletResponse mockedResponse = new MockHttpServletResponse();

    private AuthenticationManager authenticationManager;
    private FilterChain chain;
    private Address address;
    private Customer customer;
    private MyUserDetails myUserDetails;
    private List<GrantedAuthority> authorities;
    private UserPrincipal principal;

    //TODO - make sure to grab a new token from Swagger and update it here before running. It is valid only for 15 minutes.
    private String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0NCIsImZpcnN0TmFtZSI6IkhpY2N1cCIsImxhc3ROYW1lIjoiSGFkZG9jayIsImV4cCI6OTAwMDAwMTYxNTUwMjIwMSwidXNlcklkIjo4LCJpYXQiOjE2MTU1MDIyMDEsImF1dGhvcml0aWVzIjpbIkNVU1RPTUVSIl19.quGIJv0Mjjd0hUgWIf01ejIfUBWbEOkaQrdlz-ogNoE9rSh4SlHFWuk_yAANaEOlUvfuj3T2McwNPgZS9ZZo7Q";

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

        jwtAuthorizationFilter = new JWTAuthorizationFilter(authenticationManager, mockUserDetailsService);
    }

    @Test
    @DisplayName("Test that doFilterInternal does not throw exceptions")
    void test_doFilterInternal_does_not_throw_exceptions() throws ServletException, IOException {
        assertDoesNotThrow(() -> {jwtAuthorizationFilter.doFilter(mockedRequest, mockedResponse, chain);});
    }

    @Test
    @DisplayName("Test that doFilterInternal does not throw exceptions 2")
    void test_doFilterInternal_does_not_throw_exceptions_2() throws ServletException, IOException {
        when(mockUserDetailsService.loadUserByUsername("test4")).thenReturn(principal);
        mockedRequest.addHeader(HEADER_STRING, token);
        assertDoesNotThrow(() -> {jwtAuthorizationFilter.doFilter(mockedRequest, mockedResponse, chain);});
    }

    @Test
    @DisplayName("Test that doFilterInternal throws NullPointerException")
    void test_doFilterInternal_does_not_throw_NullPointerException() throws ServletException, IOException {
        principal = new UserPrincipal();
        when(mockUserDetailsService.loadUserByUsername("test4")).thenReturn(principal);
        mockedRequest.addHeader(HEADER_STRING, token);
        assertThrows(NullPointerException.class, () -> {jwtAuthorizationFilter.doFilter(mockedRequest, mockedResponse, chain);});
    }
}
