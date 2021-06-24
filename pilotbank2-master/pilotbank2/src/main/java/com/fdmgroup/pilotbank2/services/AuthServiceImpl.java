package com.fdmgroup.pilotbank2.services;

import com.fdmgroup.pilotbank2.common.PilotBankConstants;
import com.fdmgroup.pilotbank2.models.User;
import com.fdmgroup.pilotbank2.models.dto.*;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.PASSWORD_ERROR_MESSAGE;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JavaMailSender mailSender;

    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public boolean authentication(String username, String password) {
        return userRepo.existsByUsernameAndPassword(username, password);
    }

    @Override
    public User login(String username, String password) {
        return userRepo.findByUsernameAndPassword(username, password);
    }

    @Override
    public List<String> authorizeDevice(UsernameDTO username, HttpHeaders headers) {
        User user = userRepo.findByUsername(username.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                    "User with username %s not found!", username.getUsername()
                )));
        StringBuilder deviceInfo = new StringBuilder();

        if(headers != null) {
            if(headers.get("User-Agent") != null) {
                deviceInfo.append(headers.get("User-Agent").get(0));
            } else {
                throw new NullPointerException("User-Agent header should not be null!");
            }
        } else {
            throw new NullPointerException("HttpHeaders should not be null!");
        }

        user.setIsSecurityCodeVerified(false);

        List<String> strList = new ArrayList<>();

        if(deviceInfo.toString().equals(user.getDeviceInfo())) {
            user.setIsAnswerCorrect(true);
            userRepo.saveAndFlush(user);
            strList.add("Device Authorization Success");

            if(user.getEmail() != null) {
                strList.add(maskEmailAddress(user.getEmail()));
            } else {
                throw new NullPointerException("Email must not be null!");
            }

            if(user.getPhoneNumber() != null) {
                strList.add(maskPhoneNumber(user.getPhoneNumber()));
            } else {
                throw new NullPointerException("Phone number must not be null!");
            }

            return strList;
        } else {
            user.setIsAnswerCorrect(false);
            userRepo.saveAndFlush(user);
            strList.add("Device Authorization Failed");
            return strList;
        }
    }

    private String maskEmailAddress(String emailAddress) {
        StringBuilder maskedString = new StringBuilder();
        String[] parts = emailAddress.split("@");
        if(parts.length == 2 && parts[0].length() > 0 && parts[1].length() > 0) {
            if(parts[0].length() < 2) {
                maskedString.append('*');
            } else {
                maskedString.append(parts[0].substring(0, 1));
                for(int i = 1; i < parts[0].length(); i++) {
                    maskedString.append('*');
                }
            }
            maskedString.append('@');
            String[] domain = parts[1].split("\\.");
            if(domain.length == 2 && domain[0].length() > 0 && domain[1].length() > 0) {
                for(int i = 0; i < domain[0].length(); i++) {
                    maskedString.append('*');
                }
                    maskedString.append('.');
                    maskedString.append(domain[1]);
            } else {
                throw new IllegalArgumentException("The email address's format is invalid!");
            }
        } else {
            throw new IllegalArgumentException("The email address's format is invalid!");
        }
        return maskedString.toString();
    }

    private String maskPhoneNumber(String phoneNumber) {
        StringBuilder maskedString = new StringBuilder();
        if(phoneNumber.length() > 2) {
            if(phoneNumber.length() < 5) {
                maskedString.append(phoneNumber.charAt(0));
                maskedString.append("**");
                if(phoneNumber.length() == 4) {
                    maskedString.append(phoneNumber.charAt(3));
                }
            } else {
                maskedString.append(phoneNumber.substring(0, 2));
                for(int i = 2; i < phoneNumber.length()-1; i++) {
                    maskedString.append('*');
                }
                maskedString.append(phoneNumber.charAt(phoneNumber.length()-1));
            }
        } else {
            throw new NumberFormatException("The phone number is too short!");
        }
        return maskedString.toString();
    }

    @Override
    public String fetchSecurityQuestion(UsernameDTO username) {
        User user = userRepo.findByUsername(username.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "User with username %s not found!", username.getUsername())
                ));

        if (user.getAccountLockedFlag() == true) {
            throw new IllegalStateException(String.format("Account is locked. Please contact our administration."));
        } else if(LocalDateTime.now().isBefore(user.getTempLockOutExpiration())) {
            throw new IllegalStateException(String.format(
                    "Account is temporarily locked. Please wait till %s.", user.getTempLockOutExpiration().toString()
            ));
        } else {
            user.setIsSecurityCodeVerified(false);
            user.setIsAnswerCorrect(false);
            userRepo.saveAndFlush(user);
            return user.getSecurityQuestion();
        }
    }

    @Override
    public List<String> verifySecurityAnswer(SecurityAnswerDTO securityAnswer) {
        User user = userRepo.findByUsername(securityAnswer.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(String.format("User with username %s not found!", securityAnswer.getUsername())));

        if (user.getAccountLockedFlag() == true) {
            throw new IllegalStateException(String.format("Account is locked. Please contact our administration."));
        } else if(LocalDateTime.now().isBefore(user.getTempLockOutExpiration())) {
            throw new IllegalStateException(String.format("Account is temporarily locked. Please wait till %s.", user.getTempLockOutExpiration().toString()));
        } else {
            user.setIsAnswerCorrect(false);
            int incorrectAnswerCount = user.getIncorrectAnswerCount();
            List<String> strList = new ArrayList<>();

            if(user.getSecurityAnswer().equals(securityAnswer.getAnswer())) {
                user.setIsAnswerCorrect(true);
                user.setIncorrectAnswerCount(0);
                userRepo.saveAndFlush(user);
                strList.add("Correct");

                //currently not being used
                if(user.getEmail() != null) {
                    strList.add(maskEmailAddress(user.getEmail()));
                } else {
                    throw new NullPointerException("Email must not be null!");
                }

                if(user.getPhoneNumber() != null) {
                    strList.add(maskPhoneNumber(user.getPhoneNumber()));
                } else {
                    throw new NullPointerException("Phone number must not be null!");
                }
            } else {
                user.setIsAnswerCorrect(false);
                user.setIncorrectAnswerCount(++incorrectAnswerCount);
                if(incorrectAnswerCount == 1) {
                    user.setTempLockOutExpiration(LocalDateTime.now().plusMinutes(1L));
                    strList.add("Incorrect Answer");
                    strList.add("1st attempt failed");
                } else if(incorrectAnswerCount == 2) {
                    user.setTempLockOutExpiration(LocalDateTime.now().plusMinutes(2L));
                    strList.add("Incorrect Answer");
                    strList.add("2nd attempt failed");
                } else if(incorrectAnswerCount >= 3) {
                    user.setAccountLockedFlag(true);
                    strList.add("Account Locked");
                    strList.add("too many failed attempts");
                }
                userRepo.saveAndFlush(user);
            }
            return strList;
        }
    }

    @Override
    public String requestSecurityCode(SecurityCodeRequestDTO securityCodeRequest) {
        User user = userRepo.findByUsername(securityCodeRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(String.format("User with username %s not found!", securityCodeRequest.getUsername())));

        //if(securityCodeRequest.getContactInfo() == null || securityCodeRequest.getContactInfo().length() < 1) {
        //    throw new IllegalArgumentException("The email address or phone number should not be null or empty!");
        //}

        if(user.getIsAnswerCorrect() == true) {
            user.setIsAnswerCorrect(false);
            userRepo.saveAndFlush(user);
            emailSecurityCode(user);
            //did it this route due to asking for the user asking to enter email. along with text version which could not be done.
//            if(securityCodeRequest.getEmailOrText().equals("email")) {
//                if(!securityCodeRequest.getContactInfo().equals(user.getEmail())) {
//                    throw new IllegalArgumentException("The email addresses don't match!");
//                }
//                // send a security code to the user's email address
//                emailSecurityCode(user);
//            } else if(securityCodeRequest.getEmailOrText().equals("text")) {
//                if(!securityCodeRequest.getContactInfo().equals(user.getPhoneNumber())) {
//                    throw new IllegalArgumentException("The phone numbers don't match!");
//                }
//                // text a security code to the user's phone number
//                textSecurityCode(user);
//            } else {
//                throw new IllegalArgumentException(String.format("Unable to send the security code to %s!", securityCodeRequest.getEmailOrText()));
//            }
        } else {
            throw new IllegalStateException("The device must be authorized or the security question must be answer once again!");
        }
        return "Security Code Sent Successfully";
    }

    private void createSecurityCode(User user){
        int securityCode = (int)(Math.random()*999999);
        if (securityCode < 100000){
            securityCode += 1000;
        }
        System.out.println("here " + securityCode);
        user.setSecurityCode(securityCode);
    }

    private void emailSecurityCode(User user) {
        // TODO - send actual security code (must be added to user in order to be saved)
        String from = "pilot.bank.resetpass@gmail.com";
        String to = user.getEmail();
        String linkToResetPassPage = "";

        createSecurityCode(user);
        userRepo.saveAndFlush(user);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("Pilot Bank Security Code | Password Change" +
                "\n\nHi " + user.getFirstName() + "," +
                "\nVerify your Password Change Request" +
                "\n\nUse security code: \n" + user.getSecurityCode() +
                "\n\nfor Pilot Bank authentication to continue your password change process." +
                "\n\nYou are receiving this email because you requested a password change with Pilot Bank Canada." +
                "\nIf you did not request this yourself, please immediately contact our support team at: https://www.pilotbank.ca/support" +
                "\nRegards, \nPilot Bank Customer Support" +
                "\n\n*****PLEASE DON'T REPLY TO THIS EMAIL. THIS EMAIL ACCOUNT ISN'T MONITORED AND YOU WON'T GET A RESPONSE*****");

        mailSender.send(message);
    }

    private void textSecurityCode(User user) {
        // TODO - implement the business logic to text a security code to the user's phone number
    }

    @Override
    public String verifySecurityCode(SecurityCodeVerificationDTO securityCodeVerification) {
        User user = userRepo.findByUsername(securityCodeVerification.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(String.format("User with username %s not found!", securityCodeVerification.getUsername())));

        String correctSecCode = String.valueOf(user.getSecurityCode());

		  if(securityCodeVerification.getSecurityCode().equals(correctSecCode)) {
			user.setIsSecurityCodeVerified(true);
			user.setSecurityCode(0);
			userRepo.saveAndFlush(user);
            return "Security Code Verified Successfully";
		}
		else {
            return "Security Code Incorrect";
		}

    }

    @Override
    public String updatePassword(PasswordUpdateDTO passwordUpdate) {
        User user = userRepo.findByUsername(passwordUpdate.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "User with username %s not found!", passwordUpdate.getUsername()
                )));

        if(user.getIsSecurityCodeVerified() == true) {
            userRepo.saveAndFlush(user);

            if (isValidPassword(passwordUpdate.getPassword())) {
                user.setPassword(passwordEncoder.encode(passwordUpdate.getPassword()));
                user.setPasswordExpires(LocalDateTime.now().plusDays(90L));
                user.setAccountLockedFlag(false);
                user.setFailedLoginCount(0);
                user.setLastFailedLoginDate(null);
                user.setIncorrectAnswerCount(0);
                user.setIsAnswerCorrect(false);
                user.setIsSecurityCodeVerified(false);
                userRepo.saveAndFlush(user);
            } else {
                throw new IllegalArgumentException(PASSWORD_ERROR_MESSAGE);
            }
        } else {
            throw new IllegalStateException(String.format(
                    "Security code must be verified in order to update the password!"
            ));
        }
        return "Password Updated Successfully";
    }

    private boolean isValidPassword(String password) {
        boolean isValid = checkFieldIsNotBlankBeforeUpdate(password);
        if (isValid) {
            Pattern pattern = Pattern.compile(PilotBankConstants.PASSWORD_REGEX);
            Matcher matcher = pattern.matcher(password);
            return isValid && matcher.matches();
        }
        return false;
    }

    private boolean checkFieldIsNotBlankBeforeUpdate(String fieldToCheck) {
        return StringUtils.isNotBlank(fieldToCheck);
    }

}
