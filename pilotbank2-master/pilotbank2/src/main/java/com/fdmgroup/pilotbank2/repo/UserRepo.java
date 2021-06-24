package com.fdmgroup.pilotbank2.repo;

import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

	<U extends User> Optional<U> findByUserId(Long userId);

	Customer findByEmail(String email);

	Optional<Customer> findByUsername(String username);

	Customer findByUsernameAndPassword(String username, String password);
	
	User findByEmailAndPassword(String email, String password);

	boolean existsByUsername(String userName);

	boolean findByUsernameAndEmail(String userName, String email);

	boolean existsByUsernameAndPassword(String userName, String password);
}
