package com.fdmgroup.pilotbank.authentication;

import com.fdmgroup.pilotbank2.authentication.AuthenticationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockitoExtension.class})
public class AuthenticationResponseTest {

    @Test
    @DisplayName("Test that all arg constructor returns an instance with a specified value")
    void test_all_arg_constructor_returns_instance_with_specified_value() {
        AuthenticationResponse authRes = new AuthenticationResponse("testJWT");
        assertTrue(authRes instanceof AuthenticationResponse);
        assertEquals("testJWT", authRes.getJwt());
    }

    @Test
    @DisplayName("Test that getUsername returns a JWT string")
    void test_getUsername_returns_JWT_string() {
        AuthenticationResponse authRes = new AuthenticationResponse("newJWT");
        String newJwt = authRes.getJwt();
        assertEquals("newJWT", newJwt);
    }
}
