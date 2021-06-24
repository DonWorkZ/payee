package com.fdmgroup.pilotbank.authentication;

import com.fdmgroup.pilotbank2.authentication.AuthenticationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockitoExtension.class})
public class AuthenticationRequestTest {

    @Test
    @DisplayName("Test that no arg constructor returns an instance with default values")
    void test_no_arg_constructor_returns_instance_with_default_values() {
        AuthenticationRequest authReq = new AuthenticationRequest();
        assertTrue(authReq instanceof AuthenticationRequest);
        assertEquals(null, authReq.getUsername());
        assertEquals(null, authReq.getPassword());
    }

    @Test
    @DisplayName("Test that all arg constructor returns an instance with specified values")
    void test_all_arg_constructor_returns_instance_with_specified_values() {
        AuthenticationRequest authReq = new AuthenticationRequest("testUserName", "testPassword");
        assertTrue(authReq instanceof AuthenticationRequest);
        assertEquals("testUserName", authReq.getUsername());
        assertEquals("testPassword", authReq.getPassword());
    }

    @Test
    @DisplayName("Test that getUsername returns a username")
    void test_getUsername_returns_username() {
        AuthenticationRequest authReq = new AuthenticationRequest("newUserName", "testPassword");
        String authName = authReq.getUsername();
        assertEquals("newUserName", authName);
    }

    @Test
    @DisplayName("Test that setUsername updates a username")
    void test_setUsername_updates_username() {
        AuthenticationRequest authReq = new AuthenticationRequest("testUserName", "testPassword");
        authReq.setUsername("updatedUsername");
        String authName = authReq.getUsername();
        assertEquals("updatedUsername", authName);
    }

    @Test
    @DisplayName("Test that getPassword returns a password")
    void test_getPassword_returns_password() {
        AuthenticationRequest authReq = new AuthenticationRequest("testUserName", "newPassword");
        String authPassword = authReq.getPassword();
        assertEquals("newPassword", authPassword);
    }

    @Test
    @DisplayName("Test that getPassword updates a password")
    void test_setPassword_updates_password() {
        AuthenticationRequest authReq = new AuthenticationRequest("testUserName", "testPassword");
        authReq.setPassword("updatedPassword");
        String authPassword = authReq.getPassword();
        assertEquals("updatedPassword", authPassword);
    }
}
