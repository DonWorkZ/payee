package com.fdmgroup.pilotbank2.controllers;

import com.auth0.jwt.JWT;
import com.fdmgroup.pilotbank2.authentication.AuthenticationRequest;
import com.fdmgroup.pilotbank2.authentication.security.UserDetailsServiceImpl;
import com.fdmgroup.pilotbank2.authentication.security.UserPrincipal;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.User;
import com.fdmgroup.pilotbank2.models.dto.*;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import com.fdmgroup.pilotbank2.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping(value = "/users")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	UserRepo userRepo;

	@GetMapping("/")
	@SecurityRequirement(name = "jwt-bearer")
	public ResponseEntity<List<User>> getUsers() {
		List<User> userList = userService.findAll();
		return new ResponseEntity<>(userList, HttpStatus.OK);
	}

	@GetMapping("/{userId}")
	@SecurityRequirement(name = "jwt-bearer")
	public User getUserById(@PathVariable(value = "userId") Long userId) {
		return userService.findById(userId);
	}

	@PutMapping("/{userId}/update")
	@SecurityRequirement(name = "jwt-bearer")
	public <U extends User> ResponseEntity<?> updateUser(@PathVariable(value = "userId") Long userId, @RequestBody UserUpdateDTO userUpdate) {
		try {
			U user = (U) userService.updateUser(userId, userUpdate);
			return new ResponseEntity<>(user, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			String message = String.format("Error occurred while updating User with ID: %s, Error: %s", userId, e);
			return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@SecurityRequirements
	//Excludes this method from requiring bearer authentication which was applied in the main PilotBankApplication class
	@PostMapping("/customers/create")
	public ResponseEntity<?> createCustomer(@RequestBody CustomerCreationDTO userRequest) throws Exception {
		try {
			Customer customer = userService.createCustomer(userRequest);
			return new ResponseEntity<>(customer, HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			String message = String.format("Error creating customer: %s", e);
			return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@SecurityRequirement(name = "jwt-bearer")
	@DeleteMapping("/{userId}/delete")
	public ResponseEntity<?> deleteUser(@PathVariable(value = "userId") Long userId) {
		try {
			User user = userService.deleteUser(userId);
			return new ResponseEntity<>(user, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			String message = String.format("Error deleting customer: %s", e);
			return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
}
