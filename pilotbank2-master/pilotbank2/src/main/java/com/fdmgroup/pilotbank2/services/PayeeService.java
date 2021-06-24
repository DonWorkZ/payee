package com.fdmgroup.pilotbank2.services;

import com.fdmgroup.pilotbank2.models.Payee;
import com.fdmgroup.pilotbank2.models.dto.PayeeCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.PayeeUpdateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PayeeService {

	List<Payee> findAllPayees();
	Payee findByPayeeId(Long payeeId);
	Payee createPayee(PayeeCreationDTO payeeCreationRequest);
	Payee updatePayee(Long payeeId, PayeeUpdateDTO payeeUpdate);
	void deletePayee(Long payeeId);
}
