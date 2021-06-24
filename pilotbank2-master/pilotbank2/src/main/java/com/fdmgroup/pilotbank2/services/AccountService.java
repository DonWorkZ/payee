package com.fdmgroup.pilotbank2.services;

import com.fdmgroup.pilotbank2.models.Account;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.dto.AccountCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.MainAccountRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface AccountService {
	<A extends Account> A createAccount(AccountCreationDTO account);
	<A extends Account> A findAccountById(Long accountId);
	<A extends Account> A setAsMainAccount(MainAccountRequestDTO mainAccountRequest);
	List<Account> findAccountByCustomer(Customer customer);
	List<Account> findAll();
}
