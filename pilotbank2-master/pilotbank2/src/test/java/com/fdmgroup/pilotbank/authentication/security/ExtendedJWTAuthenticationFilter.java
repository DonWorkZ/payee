package com.fdmgroup.pilotbank.authentication.security;

import com.fdmgroup.pilotbank2.authentication.security.JWTAuthenticationFilter;
import com.fdmgroup.pilotbank2.authentication.security.UserDetailsServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExtendedJWTAuthenticationFilter extends JWTAuthenticationFilter {


    public ExtendedJWTAuthenticationFilter(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService) {
        super(authenticationManager, userDetailsService);
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, auth);
    }

}
