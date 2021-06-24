package com.fdmgroup.pilotbank2.services;

import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.User;
import com.fdmgroup.pilotbank2.models.dto.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Service
public interface UserService {

	// Registration
	boolean checkUniqueUser(String userName, String email);

	User validateUser(String email, String password);

	// Update
	boolean checkUniqueUsername(String userName);

	User findById(Long userId);

	Customer findCustByEmail(String email);

	List<User> findAll();

	Customer createCustomer(CustomerCreationDTO customerRequest) throws Exception;

    <U extends User> U updateUser(Long userId, UserUpdateDTO userUpdate);

	<U extends User> U deleteUser(Long userId);

}
