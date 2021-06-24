package com.fdmgroup.pilotbank2.controllers;

import com.auth0.jwt.JWT;
import com.fdmgroup.pilotbank2.authentication.AuthenticationRequest;
import com.fdmgroup.pilotbank2.authentication.security.UserDetailsServiceImpl;
import com.fdmgroup.pilotbank2.authentication.security.UserPrincipal;
import com.fdmgroup.pilotbank2.models.User;
import com.fdmgroup.pilotbank2.models.dto.*;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import com.fdmgroup.pilotbank2.services.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.fdmgroup.pilotbank2.authentication.security.SecurityConstants.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    UserRepo userRepo;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    /*** This method provided simply for ease of getting a token within Swagger-UI
     *
     * @param authRequest A JSON formatted body consisting of fields username, and password.
     * @return String "Bearer <JWT-Token>"
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authRequest, @RequestHeader HttpHeaders headers) throws Exception {
        User user = null;
        try{
            user = userRepo.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException(String.format("User with username %s not found!", authRequest.getUsername())));
        } catch (IllegalArgumentException e) {
            String message = String.format("Unable to login: %s", e);
            return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
        }

        if (user.getAccountLockedFlag() == true) {
            String message = String.format("Account is locked. Please contact our administration.");
            return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
        } else if(LocalDateTime.now().isBefore(user.getTempLockOutExpiration())) {
            String message = String.format("Account is temporarily locked. Please wait till %s.", user.getTempLockOutExpiration().toString());
            return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
        } else {
            try {
                Authentication userAuth = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

                if (userAuth != null) {
                    Date issuedTime = new Date(System.currentTimeMillis());
                    List<GrantedAuthority> authoritiesToConvert = (List<GrantedAuthority>) userDetailsService.loadUserByUsername(
                            ((UserPrincipal) userAuth.getPrincipal()).getUsername()).getAuthorities();

                    List<String> authorities = userDetailsService.convertGrantedAuthoritiesToStrings(authoritiesToConvert);
                    String token = JWT.create()
                            .withSubject(((UserPrincipal) userAuth.getPrincipal()).getUsername())
                            .withClaim("userId", ((UserPrincipal) userAuth.getPrincipal()).getId())
                            .withClaim("firstName", ((UserPrincipal) userAuth.getPrincipal()).getFirstName())
                            .withClaim("lastName", ((UserPrincipal) userAuth.getPrincipal()).getLastName())
                            .withClaim("authorities", authorities)
                            .withIssuedAt(issuedTime)
                            .withExpiresAt(new Date(issuedTime.getTime() + EXPIRATION_TIME))
                            .sign(HMAC512(SECRET.getBytes()));

                    if(headers != null) {
                        if(headers.get("User-Agent") != null) {
                            user.setDeviceInfo(headers.get("User-Agent").get(0));
                        } else {
                            user.setDeviceInfo(null);
                        }
                    } else {
                        user.setDeviceInfo(null);
                    }
                    userRepo.saveAndFlush(user);

                    return new ResponseEntity<>(TOKEN_PREFIX + token, HttpStatus.OK);
                }
            }  catch (DisabledException | CredentialsExpiredException | LockedException | AccountExpiredException | BadCredentialsException e) {
                String message = String.format("Unable to login: %s", e);
                return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>("", HttpStatus.OK);  //This code should never be reached
    }

    @SecurityRequirements
    @PostMapping("/authorizeDevice")
    public ResponseEntity<?> authorizeDevice(@RequestBody UsernameDTO username, @RequestHeader HttpHeaders headers) {
        try {
            List<String> responseStr = authService.authorizeDevice(username, headers);
            return new ResponseEntity<>(responseStr, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException e) {
            String message = String.format("Error authorizing the device: %s", e);
            return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @SecurityRequirements
    @PostMapping("/initiateTwoStepVerification")
    public ResponseEntity<?> fetchSecurityQuestion(@RequestBody UsernameDTO username) {
        try {
            String securityQuestion = authService.fetchSecurityQuestion(username);
            return new ResponseEntity<>(securityQuestion, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException e) {
            String message = String.format("Error fetching the security question: %s", e);
            return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @SecurityRequirements
    @PostMapping("/answerSecurityQuestion")
    public ResponseEntity<?> verifySecurityAnswer(@RequestBody SecurityAnswerDTO securityAnswer) {
        try {
            List<String> attemptResult = authService.verifySecurityAnswer(securityAnswer);
            return new ResponseEntity<>(attemptResult, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException e) {
            String message = String.format("Error verifying the security answer: %s", e);
            return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @SecurityRequirements
    @PostMapping("/requestSecurityCode")
    public ResponseEntity<?> requestSecurityCode(@RequestBody SecurityCodeRequestDTO securityCodeRequest) {
        try {
            String responseStr = authService.requestSecurityCode(securityCodeRequest);
            return new ResponseEntity<>(responseStr, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException e) {
            String message = String.format("Error requesting the security code: %s", e);
            return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @SecurityRequirements
    @PostMapping("/verifySecurityCode")
    public ResponseEntity<?> verifySecurityCode(@RequestBody SecurityCodeVerificationDTO securityCodeVerification) {
        try {
            String responseStr = authService.verifySecurityCode(securityCodeVerification);
            return new ResponseEntity<>(responseStr, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException e) {
            String message = String.format("Error verifying the security code: %s", e);
            return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @SecurityRequirements
    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateDTO passwordUpdate) {
        try {
            String responseStr = authService.updatePassword(passwordUpdate);
            return new ResponseEntity<>(responseStr, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException e) {
            String message = String.format("Error updating the password: %s", e);
            return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
