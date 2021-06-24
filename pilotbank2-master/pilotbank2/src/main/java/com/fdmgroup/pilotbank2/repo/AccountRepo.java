package com.fdmgroup.pilotbank2.repo;

import com.fdmgroup.pilotbank2.models.Account;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long>{

	List<Account> findByOwnedAccountCustomer(Customer customer);
	List<Account> findByAccountType(AccountTypeEnum accountTypeEnum);
	Optional<Account> findByAccountId(Long accountId);
}
