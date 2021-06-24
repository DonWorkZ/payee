package com.fdmgroup.pilotbank2.repo;

import com.fdmgroup.pilotbank2.models.Payee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayeeRepo extends JpaRepository<Payee, Long> {
	Payee findByCustomer_UserId(Long customerId);
	List<Payee> findAllByCustomer_UserId(Long customerId);
	Optional<Payee> findByPayeeId(Long payeeId);
	void deleteById(Long payeeId);
}
