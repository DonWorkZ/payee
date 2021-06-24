package com.fdmgroup.pilotbank2.services;

import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.Payee;
import com.fdmgroup.pilotbank2.models.User;
import com.fdmgroup.pilotbank2.models.dto.PayeeCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.PayeeUpdateDTO;
import com.fdmgroup.pilotbank2.repo.PayeeRepo;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayeeServiceImpl implements PayeeService {

	@Autowired
	private PayeeRepo payeeRepo;

	@Autowired
	private UserRepo userRepo;

	@Override
	public List<Payee> findAllPayees() {
		return payeeRepo.findAll();
	}

	@Override
	public Payee findByPayeeId(Long payeeId) {
		Payee payee = payeeRepo.findByPayeeId(payeeId)
				.orElseThrow(() -> new IllegalStateException(String.format("Payee with ID: %s not found", payeeId)));
		return payee;
	}

	@Override
	public Payee createPayee(PayeeCreationDTO payeeCreationRequest) throws IllegalStateException {
		Customer customerActual = (Customer) userRepo.findByUserId(payeeCreationRequest.getCustomerId())
				.orElseThrow(() -> new IllegalStateException(String.format("User with ID: %s not found", payeeCreationRequest.getCustomerId())));

		Payee payeeActual = Payee.builder()
				.companyName(payeeCreationRequest.getCompanyName())
				.accountNumber(payeeCreationRequest.getAccountNumber())
				.build();

		if (StringUtils.isNotBlank(payeeCreationRequest.getPostalCode())){
			payeeActual.setPostalCode(payeeCreationRequest.getPostalCode());
		}

		if (StringUtils.isNotBlank(payeeCreationRequest.getNickname())){
			payeeActual.setNickname(payeeCreationRequest.getNickname());
		}

		customerActual.addToPayees(payeeActual);
		userRepo.saveAndFlush((User) customerActual);
		return customerActual.getPayees().get(customerActual.getPayees().size() - 1);
	}

	@Override
	public Payee updatePayee(Long payeeId, PayeeUpdateDTO payeeUpdate) {
		Payee payeeActual = payeeRepo.findByPayeeId(payeeId)
				.orElseThrow(() -> new IllegalStateException(String.format("Payee with ID: %s not found", payeeId)));
		updatePayeeInformation(payeeUpdate, payeeActual);
		return payeeRepo.saveAndFlush(payeeActual);
	}

	@Override
	public void deletePayee(Long payeeId) {
		Payee payee = payeeRepo.findByPayeeId(payeeId)
				.orElseThrow(() -> new IllegalStateException(String.format("Payee with ID: %s not found", payeeId)));
		Customer customer = (Customer) userRepo.findById(payee.getCustomer().getUserId())
				.orElseThrow(() -> new IllegalStateException(String.format("Customer with ID: %s not found", payee.getCustomer().getUserId())));
		customer.removePayee(payee);
		payeeRepo.saveAndFlush(payee);
	}

	private void updatePayeeInformation(PayeeUpdateDTO payeeUpdate, Payee payeeActual) {
		if (checkFieldIsNotBlankBeforeUpdate(payeeUpdate.getCompanyName())) {
			payeeActual.setCompanyName(payeeUpdate.getCompanyName());
		}

		if (checkFieldIsNotBlankBeforeUpdate(payeeUpdate.getAccountNumber())){
			payeeActual.setAccountNumber(payeeUpdate.getAccountNumber());
		}

		if (checkFieldIsNotBlankBeforeUpdate(payeeUpdate.getPostalCode())){
			payeeActual.setPostalCode(payeeUpdate.getPostalCode());
		}

		if (checkFieldIsNotBlankBeforeUpdate(payeeUpdate.getNickname())){
			payeeActual.setNickname(payeeUpdate.getNickname());
		}
	}

	private boolean checkFieldIsNotBlankBeforeUpdate(String fieldToCheck){
		return StringUtils.isNotBlank(fieldToCheck);
	}

}
