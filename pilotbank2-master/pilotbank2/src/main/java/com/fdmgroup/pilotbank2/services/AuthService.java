package com.fdmgroup.pilotbank2.services;

import com.fdmgroup.pilotbank2.models.User;
import com.fdmgroup.pilotbank2.models.dto.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AuthService {
    // Login
    boolean authentication(String userName, String password);

    User login(String userName, String password);

    List<String> authorizeDevice(UsernameDTO username, HttpHeaders headers);

    String fetchSecurityQuestion(UsernameDTO username);

    List<String> verifySecurityAnswer(SecurityAnswerDTO securityAnswer);

    String requestSecurityCode(SecurityCodeRequestDTO securityCodeRequest);

    String verifySecurityCode(SecurityCodeVerificationDTO securityCodeVerification);

    String updatePassword(PasswordUpdateDTO passwordUpdate);
}
