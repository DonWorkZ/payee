package com.fdmgroup.pilotbank2.authentication.security;

import com.fdmgroup.pilotbank2.models.User;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class AuthenticationEventListener {

	@Autowired
	private UserRepo userRepo;

	@EventListener
	public void authenticationFailed(AuthenticationFailureBadCredentialsEvent event){
		String username = (String) event.getAuthentication().getPrincipal();
		User userActual = userRepo.findByUsername(username)
				.orElseThrow(() -> new IllegalStateException(String.format("User with username %s not found", username)));
		int currentFailedLoginCount = userActual.getFailedLoginCount();
		userActual.setFailedLoginCount(currentFailedLoginCount + 1);
		userActual.setLastFailedLoginDate(LocalDateTime.now());
		userRepo.save(userActual);
	}

	@EventListener
	public void authenticationSucceeded(AuthenticationSuccessEvent event){
		UserPrincipal userPrincipal = (UserPrincipal) event.getAuthentication().getPrincipal();
		User userActual = userRepo.findByUsername(userPrincipal.getUsername())
				.orElseThrow(() -> new IllegalStateException(String.format("User with username %s not found", userPrincipal.getUsername())));
		userActual.setFailedLoginCount(0);
		userActual.setLastFailedLoginDate(null);
		userRepo.save(userActual);
	}
}
