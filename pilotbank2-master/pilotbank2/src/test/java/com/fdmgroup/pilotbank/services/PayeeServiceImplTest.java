package com.fdmgroup.pilotbank.services;

import com.fdmgroup.pilotbank2.models.Address;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.Payee;
import com.fdmgroup.pilotbank2.models.User;
import com.fdmgroup.pilotbank2.models.dto.PayeeCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.PayeeUpdateDTO;
import com.fdmgroup.pilotbank2.repo.PayeeRepo;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import com.fdmgroup.pilotbank2.services.PayeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PayeeServiceImplTest {

	@InjectMocks
	private PayeeServiceImpl payeeService;

	@Mock
	private PayeeRepo mockPayeeRepo;

	@Mock
	private UserRepo mockUserRepo;

	private Customer customer;
	private Address address;
	private Payee payee;

	@BeforeEach
	void init() {
		address = Address.builder()
				.streetNumber("626").streetName("Model Citizen Ln.").suiteNumber(null)
				.city("Oahu").province("Hawaii").postalCode("62626").build();

		customer = Customer.builder()
				.userId(123L)
				.build();

		payee = Payee.builder()
				.payeeId(987L)
				.companyName("TestPayee")
				.nickname("TestNickName")
				.postalCode("74123")
				.accountNumber("258741")
				.customer(customer)
				.isActive(true)
				.transactions(new ArrayList<>())
				.build();
	}

	@Test
	@DisplayName("Test that findAllPayee returns a list of payees")
	void testThatFindAllPayeeReturnsPayeeList() {
		when(mockPayeeRepo.findAll()).thenReturn(Arrays.asList(payee));
		List<Payee> returnedPayeeList = payeeService.findAllPayees();
		verify(mockPayeeRepo, times(1)).findAll();
		assertEquals(1, returnedPayeeList.size());
		assertEquals(payee.getCompanyName(), returnedPayeeList.get(0).getCompanyName());
	}

	@Test
	@DisplayName("Test that findAllPayee returns a list of payees")
	void testThatFindByPayeeIdReturnsCorrectPayee() {
		when(mockPayeeRepo.findByPayeeId(payee.getPayeeId())).thenReturn(Optional.of(payee));
		payeeService.findByPayeeId(payee.getPayeeId());
		verify(mockPayeeRepo, times(1)).findByPayeeId(payee.getPayeeId());
		assertDoesNotThrow(()-> mockPayeeRepo.findByPayeeId(payee.getPayeeId()));
	}

	@Test
	@DisplayName("Test that findByPayeeIdThrowsIllegalStateExceptionWithInvalidId")
	void testThatFindByPayeeIdThrowsExceptionWithInvalidId(){
		Exception exception = assertThrows(IllegalStateException.class, ()-> payeeService.findByPayeeId(1L));
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains("Payee with ID: 1 not found"));
	}

	@Test
	@DisplayName("Test that createPayee Returns the latest Payee")
	void testThatCreatePayeeReturnsLatestPayee(){
		when(mockUserRepo.findByUserId(customer.getUserId())).thenReturn(Optional.of(customer));
		PayeeCreationDTO payeeCreation = PayeeCreationDTO.builder().companyName("TestPayee")
				.accountNumber("258741").postalCode("258741").customerId(123L).nickname("NickPayee").build();
		Payee payeeReturned = payeeService.createPayee(payeeCreation);
		verify(mockUserRepo, times(1)).saveAndFlush((User) customer);
		assertEquals(customer.getPayees().get(customer.getPayees().size()-1).getCompanyName(), payeeReturned.getCompanyName());
	}

	@Test
	@DisplayName("Test that updatePayee returns updated payee information")
	void testThatUpdatePayeeReturnsUpdatedPayee() {
		PayeeUpdateDTO payeeUpdate = PayeeUpdateDTO.builder().companyName("UpdatedPayee")
				.accountNumber("258741").postalCode("99999").nickname("NewNickName").build();
		when(mockPayeeRepo.findByPayeeId(payee.getPayeeId())).thenReturn(Optional.of(payee));
		payeeService.updatePayee(987L, payeeUpdate);
		verify(mockPayeeRepo, times(1)).findByPayeeId(payee.getPayeeId());
		assertEquals(payeeUpdate.getCompanyName(), payee.getCompanyName());
		assertEquals(payeeUpdate.getPostalCode(), payee.getPostalCode());
		assertEquals(payeeUpdate.getAccountNumber(), payee.getAccountNumber());
		assertEquals(payeeUpdate.getNickname(), payee.getNickname());
	}

	@Test
	@DisplayName("Test that deletePayee sets inactive and removes from payee list")
	void testThatDeletePayeeSetsInactiveAndRemovesFromPayeeList() {
		when(mockPayeeRepo.findByPayeeId(payee.getPayeeId())).thenReturn(Optional.of(payee));
		when(mockUserRepo.findById(customer.getUserId())).thenReturn(Optional.of(customer));
		payeeService.deletePayee(payee.getPayeeId());
		verify(mockPayeeRepo, times(1)).findByPayeeId(payee.getPayeeId());
		verify(mockUserRepo, times(1)).findById(customer.getUserId());
		assertEquals(0, customer.getPayees().size());
		assertEquals(false, payee.isActive());
	}
	
	@Test
	@DisplayName("Test that deletePayee Throws IllegalStateExceptionWithInvalidPayeeId")
	void testThatDeletePayeeThrowsIllegalStateExceptionWithInvalidPayeeId() {
		Exception exception = assertThrows(IllegalStateException.class, () -> payeeService.deletePayee(777L));
		String message = exception.getMessage();
		assertTrue(message.contains("Payee with ID: 777 not found"));
	}

	@Test
	@DisplayName("Test that deletePayee Throws IllegalStateExceptionWithInvalidUserId")
	void testThatDeletePayeeThrowsIllegalStateExceptionWithInvalidUserId() {
		when(mockPayeeRepo.findByPayeeId(payee.getPayeeId())).thenReturn(Optional.of(payee));
		Exception exception = assertThrows(IllegalStateException.class, () -> payeeService.deletePayee(payee.getPayeeId()));
		String message = exception.getMessage();
		assertTrue(message.contains("Customer with ID: 123 not found"));
	}
}
