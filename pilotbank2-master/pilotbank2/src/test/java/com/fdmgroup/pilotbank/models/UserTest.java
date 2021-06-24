package com.fdmgroup.pilotbank.models;

import com.fdmgroup.pilotbank2.models.*;
import com.fdmgroup.pilotbank2.models.dto.AccountCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.CustomerCreationDTO;
import com.fdmgroup.pilotbank2.services.UserServiceImpl;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.CHECKING_MONTHLY_TRANSACTION_LIMIT;
import static com.fdmgroup.pilotbank2.common.PilotBankConstants.PASSWORD_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class UserTest {

	@Spy
	private UserServiceImpl mockUserService;

	private CustomerCreationDTO customerCreationRequest, invalidCustomerCreationRequest;
	private AccountCreationDTO accountCreationDTO;
	private Customer customer;
	private Address address;
	private Checking checking;

	@BeforeEach
	void init(){
		address = Address.builder()
				.addressId(999L)
				.streetNumber("501").streetName("Flameburst St.").city("Dragontown")
				.province("Berk").postalCode("ABC123")
				.build();

		accountCreationDTO = AccountCreationDTO.builder()
				.openedByCustomerId(5L).accountType(AccountTypeEnum.CHECKING.toString())
				.balance(BigDecimal.valueOf(450)).isMainAccount(true)
				.build();

		customerCreationRequest = CustomerCreationDTO.builder()
				.username("AlphaNightfury").title("Mr.").firstName("Toothless").lastName("Haddock")
			    .email("toothless.haddock@gmail.com").phoneNumber("111-111-1111").password("1A2b3c4!")
			    .role("CUSTOMER").sin("111-111-113").industry("Homeland Security")
			    .occupation("Security Officer").securityQuestion("What is your favorite food?")
				.securityAnswer("pizza")
				.address(address)
			    .account(accountCreationDTO)
				.build();

		checking = Checking.builder()
				.accountId(999L)
				.accountType(AccountTypeEnum.CHECKING)
				.balance(customerCreationRequest.getAccount().getBalance())
				.monthlyTransactionsRemaining(CHECKING_MONTHLY_TRANSACTION_LIMIT)
				.hasMonthlyServiceFee(false)
				.isMainAccount(true)
				.build();

		 customer = Customer.builder()
				 .userId(5L).username("AlphaNightfury").title("Mr.").firstName("Toothless").lastName("Haddock")
				 .email("toothless.haddock@gmail.com").phoneNumber("111-111-1111").password("1A2b3c4!")
				 .role("CUSTOMER").sin("111-111-113").industry("Homeland Security")
				 .occupation("Security Officer").isActive(true).securityQuestion("What is your favorite food?")
				 .securityAnswer("pizza")
				 .mainAccount(checking)
				 .build();
	}

	@Test
	@DisplayName("Test that a newly created Customer gets an address attached")
	void testThatANewUserHasAnAddress() throws Exception {
		doReturn(customer).when(mockUserService).createCustomer(customerCreationRequest);
		mockUserService.createCustomer(customerCreationRequest);
		assertNotNull(customer.getAddressList());
	}

	@Test
	@DisplayName("Test that a newly created Customer's account becomes their main account")
	void testThatANewCustomerHasAMainAccount() throws Exception {
		doReturn(customer).when(mockUserService).createCustomer(customerCreationRequest);
		mockUserService.createCustomer(customerCreationRequest);
		mockUserService.addAccountsToCustomer(customer, checking);
		assertEquals(checking, customer.getMainAccount());
	}

	@Test
	@DisplayName("Test that a newly created Customer's account becomes their main account")
	void testThatANewCustomerOwnedAccountsIsNotNull() throws Exception {
		customer.addToOwnedAccounts(checking);
		doReturn(customer).when(mockUserService).createCustomer(customerCreationRequest);
		mockUserService.createCustomer(customerCreationRequest);
		assertNotNull(customer.getOwnedAccounts());
		assertEquals(1, customer.getOwnedAccounts().size());
		assertNotNull(customer.getMainAccount());
	}

	@Test
	@DisplayName("Test a newly created Customer has a password expiration")
	void testThatANewCustomerHasAPasswordExpiration() throws Exception{
		doReturn(customer).when(mockUserService).createCustomer(customerCreationRequest);
		mockUserService.createCustomer(customerCreationRequest);
		assertNotNull(customer.getPasswordExpires());
	}

	@Test
	@DisplayName("Test an exception is thrown when a password does not meet complexity requirements")
	void testAnExceptionIsThrownWhenAPasswordDoesNotMeetComplexity(){
		invalidCustomerCreationRequest = CustomerCreationDTO.builder()
				.username("AlphaNightfury").title("Mr.").firstName("Toothless").lastName("Haddock")
				.email("toothless.haddock@gmail.com").phoneNumber("111-111-1111").password("abc123")
				.role("CUSTOMER").sin("111-111-113").industry("Homeland Security")
				.occupation("Security Officer").address(address).account(accountCreationDTO)
				.build();

		Exception exception = assertThrows(IllegalArgumentException.class, () -> mockUserService.createCustomer(invalidCustomerCreationRequest));
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(PASSWORD_ERROR_MESSAGE));
	}

	@Test
	@DisplayName("Test if the user has expected values")
	void test_if_user_has_expected_values() {
		assertEquals("111-111-113", customer.getSin());
		assertEquals("Mr.", customer.getTitle());
		assertEquals("What is your favorite food?", customer.getSecurityQuestion());
		assertEquals("pizza", customer.getSecurityAnswer());
		assertEquals(0, customer.getIncorrectAnswerCount());
	}

	@Test
	@DisplayName("Test if toString() returns the expected string")
	void test_if_toString_returns_expected_string() {
		doReturn(customer).when(mockUserService).createCustomer(customerCreationRequest);
		User customer1 = (User) mockUserService.createCustomer(customerCreationRequest);
		String expectedStr = "User [userId=" + 5L + ", title=" + "Mr." + ", username=" + "AlphaNightfury" + ", firstName=" + "Toothless"
				+ ", lastName=" + "Haddock" + ", email=" + "toothless.haddock@gmail.com" + ", phoneNumber=" + "111-111-1111" + ", sin=" + "111-111-113"
				+ "role= " + "CUSTOMER" + " accountActive: " + true + ", accountExpires=" + null + "]" + "Customer [income=" + null + "]";
		assertEquals(expectedStr, customer1.toString());
	}

	@Test
	@DisplayName("Test if removeAddress() removes an address from a user")
	void test_if_removeAddress_removes_address_from_user() {
		doReturn(customer).when(mockUserService).createCustomer(customerCreationRequest);
		User customer1 = (User) mockUserService.createCustomer(customerCreationRequest);
		customer1.removeAddress(address);
		assertEquals(0, customer1.getAddressList().size());
	}
}
