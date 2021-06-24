package com.fdmgroup.pilotbank2.authentication.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.fdmgroup.pilotbank2.authentication.security.SecurityConstants.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private UserRepo userRepo;

    private AuthenticationManager authenticationManager;
    private UserDetailsServiceImpl userDetailsService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws BadCredentialsException {
        try {
            UserPrincipal user = (UserPrincipal) new ObjectMapper().readValue(request.getInputStream(), UserPrincipal.class);

            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword(),
                        new ArrayList<>()
                    )
            );
        } catch (BadCredentialsException | IOException e) {
           throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth)
            throws IOException, ServletException{
        Date issuedTime = new Date(System.currentTimeMillis());
        String username = ((UserPrincipal) auth.getPrincipal()).getUsername();
        List<GrantedAuthority> authoritiesToConvert = (List<GrantedAuthority>) userDetailsService.loadUserByUsername(username).getAuthorities();
        List<String> authorities = userDetailsService.convertGrantedAuthoritiesToStrings(authoritiesToConvert);

        String token = JWT.create()
                .withSubject(((UserPrincipal) auth.getPrincipal()).getUsername())
                .withClaim("userId", ((UserPrincipal) auth.getPrincipal()).getId())
                .withClaim("firstName", ((UserPrincipal) auth.getPrincipal()).getFirstName())
                .withClaim("lastName", ((UserPrincipal) auth.getPrincipal()).getLastName())
                .withClaim("authorities", authorities)
                .withIssuedAt(issuedTime)
                .withExpiresAt(new Date(issuedTime.getTime() + EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }

}
