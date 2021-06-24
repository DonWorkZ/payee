package com.fdmgroup.pilotbank2.controllers;

import com.fdmgroup.pilotbank2.models.Payee;
import com.fdmgroup.pilotbank2.models.dto.PayeeCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.PayeeUpdateDTO;
import com.fdmgroup.pilotbank2.services.PayeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/payees")
public class PayeeController {

	@Autowired
	private PayeeService payeeService;

	@GetMapping("/")
	@SecurityRequirement(name = "jwt-bearer")
	public ResponseEntity<?> findAllPayees(){
		List<Payee> payees = payeeService.findAllPayees();
		return new ResponseEntity(payees, HttpStatus.OK);
	}

	@PostMapping("/create")
	@SecurityRequirement(name = "jwt-bearer")
	public ResponseEntity<?> createPayee(@RequestBody PayeeCreationDTO payeeCreationRequest) {
		try {
			Payee payee = payeeService.createPayee(payeeCreationRequest);
			return new ResponseEntity(payee, HttpStatus.CREATED);
		} catch (IllegalStateException e){
			String message = String.format("Error creating Payee: %s", e);
			return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@GetMapping("/{payeeId}")
	@SecurityRequirement(name = "jwt-bearer")
	public ResponseEntity<?> findByPayeeId(@PathVariable(value = "payeeId") Long payeeId) {
		try {
			Payee payee = payeeService.findByPayeeId(payeeId);
			return new ResponseEntity(payee, HttpStatus.OK);
		} catch (IllegalStateException e) {
			String message = String.format("Error processing Payee: %s", e);
			return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@PutMapping("/{payeeId}")
	@SecurityRequirement(name = "jwt-bearer")
	public ResponseEntity<?> updatePayee(@PathVariable(value = "payeeId") Long payeeId, @RequestBody PayeeUpdateDTO payeeUpdate){
		try {
			Payee updatedPayee = payeeService.updatePayee(payeeId, payeeUpdate);
			return new ResponseEntity(updatedPayee, HttpStatus.OK);
		} catch (IllegalStateException e) {
			String message = String.format("Unable to update Payee: %s", e);
			return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@DeleteMapping("/{payeeId}")
	@SecurityRequirement(name = "jwt-bearer")
	public ResponseEntity<?> deletePayee(@PathVariable(value = "payeeId") Long payeeId){
		try{
			payeeService.deletePayee(payeeId);
			String message = String.format("Payee ID: %s, deleted successfully", payeeId);
			return new ResponseEntity(message, HttpStatus.OK);
		} catch (IllegalStateException e) {
			String message = String.format("Error deleting payee: %s", e);
			return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

}
