package com.fdmgroup.pilotbank.services;

import com.fdmgroup.pilotbank2.models.*;
import com.fdmgroup.pilotbank2.models.dto.*;
import com.fdmgroup.pilotbank2.repo.AccountRepo;
import com.fdmgroup.pilotbank2.repo.AddressRepo;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import com.fdmgroup.pilotbank2.services.UserServiceImpl;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import com.fdmgroup.pilotbank2.type.TransactionMemoEnum;
import com.fdmgroup.pilotbank2.type.TransactionTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.ISO_DATE_TIME;
import static com.fdmgroup.pilotbank2.common.PilotBankConstants.PASSWORD_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class UserServiceImplTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepo mockUserRepo;

	@Mock
	private PasswordEncoder mockPasswordEncoder;

	private AccountCreationDTO accountCreationDTO;
	private Address address, newAddress;
	private Checking checking;
	private Savings savings;
	private Student student;
	private FirstClassChecking firstClassChecking;
	private PremiumVisa premiumVisa;
	private BusinessVisa businessVisa;
	private Customer customer;
	private CustomerCreationDTO customerCreationRequest;
	private UserUpdateDTO userUpdate;
	private Transaction transaction;
	private UsernameDTO usernameDTO;
	private SecurityAnswerDTO securityAnswerDTO;
	private SecurityCodeRequestDTO securityCodeRequestDTO;
	private SecurityCodeVerificationDTO securityCodeVerificationDTO;
	private PasswordUpdateDTO passwordUpdateDTO;
	private HttpHeaders headers = new HttpHeaders();

	@BeforeEach
	void init() {

		address = Address.builder()
				.streetNumber("626").streetName("Model Citizen Ln.").suiteNumber(null)
				.city("Oahu").province("Hawaii").postalCode("62626").build();

		accountCreationDTO = AccountCreationDTO.builder()
				.openedByCustomerId(5L).accountType(AccountTypeEnum.CHECKING.toString())
				.balance(BigDecimal.valueOf(450)).isMainAccount(true)
				.build();

		customerCreationRequest = CustomerCreationDTO.builder()
				.username("Experiment626").title("Mr.").firstName("Stitch").lastName("Pelakai")
				.email("experiment626@gmail.com").phoneNumber("626-626-6266").password("1A2b3c4!")
				.role("CUSTOMER").sin("626-626-626").industry("Entertainment")
				.occupation("Elvis Impersonator")
				.address(address)
				.account(accountCreationDTO)
				.build();

		checking = Checking.builder()
				.accountId(5L)
				.isMainAccount(true)
				.accountType(AccountTypeEnum.CHECKING)
				.balance(customerCreationRequest.getAccount().getBalance())
				.allTransactions(new ArrayList<>())
				.ownedAccountCustomer(customer)
				.build();

		savings = Savings.builder()
				.accountId(6L)
				.accountType(AccountTypeEnum.SAVINGS)
				.balance(customerCreationRequest.getAccount().getBalance())
				.ownedAccountCustomer(customer)
				.isMainAccount(true)
				.allTransactions(new ArrayList<>())
				.build();

		student = Student.builder()
				.accountId(7L)
				.accountType(AccountTypeEnum.STUDENT)
				.balance(customerCreationRequest.getAccount().getBalance())
				.ownedAccountCustomer(customer)
				.isMainAccount(true)
				.allTransactions(new ArrayList<>())
				.build();

		firstClassChecking = FirstClassChecking.builder()
				.accountId(8L)
				.accountType(AccountTypeEnum.FIRST_CLASS_CHECKING)
				.balance(customerCreationRequest.getAccount().getBalance())
				.ownedAccountCustomer(customer)
				.isMainAccount(true)
				.allTransactions(new ArrayList<>())
				.build();

		premiumVisa = PremiumVisa.builder()
				.accountId(9L)
				.accountType(AccountTypeEnum.PREMIUM_VISA)
				.ownedAccountCustomer(customer)
				.isMainAccount(true)
				.allTransactions(new ArrayList<>())
				.build();

		businessVisa = BusinessVisa.builder()
				.accountId(0L)
				.accountType(AccountTypeEnum.BUSINESS_VISA)
				.ownedAccountCustomer(customer)
				.isMainAccount(true)
				.allTransactions(new ArrayList<>())
				.build();

		transaction = Transaction.builder()
				.account(checking)
				.transactionDate(LocalDateTime.parse(LocalDateTime.now().toString(), ISO_DATE_TIME))
				.amount(checking.getBalance())
				.transactionType(TransactionTypeEnum.CREDIT)
				.transactionMemo(TransactionMemoEnum.INITIAL_DEPOSIT)
				.build();

		customer = Customer.builder()
				.userId(626L).title("Mr.").firstName("Stitch").lastName("Pelakai").username("Experiment626")
				.email("experiment626@gmail.com").phoneNumber("626-626-6266")
				.sin("{bcrypt}$2a$10$mWkWlyaB89vtZPwRk0Gb1u3GZK08mDDc04tL/2Air0RFz.j0.2MR6")
				.password("{bcrypt}$2a$10$flvQjNBf1Fwd3ETm6Ttf8eVKPhvSzCGGj5xYBKOLTqRtwdmqwlGDu")
				.passwordExpires(LocalDateTime.of(2021,02,02,00,00,00,00)
					.plusDays(90L))
				.tempLockOutExpiration(LocalDateTime.parse("1970-01-01T00:00:00"))
				.role("CUSTOMER").isActive(true).lastFailedLoginDate(null).accountLockedFlag(false)
				.securityQuestion("What is your favorite food?").securityAnswer("pizza")
				.addressList(Arrays.asList(address))
				.accountExpires(null).industry("Entertainment").occupation("Elvis Impersonator").build();

		newAddress = Address.builder()
				.streetNumber("888").streetName("New User Way").suiteNumber("222")
				.city("New York").province("NY").postalCode("10000").build();

		userUpdate = UserUpdateDTO.builder()
				.email("cuteandfluffy@gmail.com")
				.build();

		usernameDTO = UsernameDTO.builder()
				.username(customer.getUsername())
				.build();

		securityAnswerDTO = SecurityAnswerDTO.builder()
				.username(customer.getUsername())
				.answer(customer.getSecurityAnswer())
				.build();

		securityCodeRequestDTO = SecurityCodeRequestDTO.builder()
				.username(customer.getUsername())
				//.emailOrText("email")
				.build();

		securityCodeVerificationDTO = SecurityCodeVerificationDTO.builder()
				.username(customer.getUsername())
				.securityCode("testCode1234")
				.build();

		passwordUpdateDTO = PasswordUpdateDTO.builder()
				.username(customer.getUsername())
				.password("newPW123!")
				.build();
	}

	@Test
	@DisplayName("Test that a user is Unique")
	void testThatAUserIsUnique(){
		when(mockUserRepo.findByUsernameAndEmail(customer.getUsername(), customer.getEmail())).thenReturn(true);
		userService.checkUniqueUser(customer.getUsername(), customer.getEmail());
		verify(mockUserRepo, times(1)).findByUsernameAndEmail(customer.getUsername(), customer.getEmail());
	}

	@Test
	@DisplayName("Test that a username is Unique")
	void testThatAUsernameIsUnique(){
		when(mockUserRepo.existsByUsername(customer.getUsername())).thenReturn(true);
		userService.checkUniqueUsername(customer.getUsername());
		verify(mockUserRepo, times(1)).existsByUsername(customer.getUsername());
	}

	@Test
	@DisplayName("Test a customer can be found by e-mail address")
	void testThatACustomerCanBeFoundByEmail(){
		when(mockUserRepo.findByEmail(customer.getEmail())).thenReturn(customer);
		userService.findCustByEmail(customer.getEmail());
		verify(mockUserRepo, times(1)).findByEmail(customer.getEmail());
	}

	@Test
	@DisplayName("Test findAll returns a list of Users")
	void testThatFindAllReturnsAListOfUsers(){
		when(mockUserRepo.findAll()).thenReturn(Arrays.asList(customer));
		userService.findAll();
		verify(mockUserRepo, times(1)).findAll();
		assertEquals(1, mockUserRepo.findAll().size());
	}

	@Test
	@DisplayName("Test providing a correct userId returns a user")
	void testProvidingACorrectUserIdReturnsAUser(){
		when(mockUserRepo.findByUserId(customer.getUserId())).thenReturn(Optional.of(customer));
		userService.findById(customer.getUserId());
		verify(mockUserRepo, times(1)).findByUserId(customer.getUserId());
		assertDoesNotThrow(()-> mockUserRepo.findByUserId(customer.getUserId()));
	}

	@Test
	@DisplayName("Test providing an incorrect userId throws an IllegalStateException")
	void testProvidingInvalidUserIdThrowsInvalidStateException(){
		Exception exception = assertThrows(IllegalStateException.class, () -> userService.findById(42L));

		String expectedMessage = String.format("User with ID: %s not found", 42L);
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	@DisplayName("(Validate User) Test providing an email and password returns a user")
	void testProvidingAnEmailAndPasswordReturnsAUser(){
		when(mockUserRepo.findByEmailAndPassword(customer.getEmail(), customer.getPassword())).thenReturn((customer));
		userService.validateUser(customer.getEmail(), customer.getPassword());
		verify(mockUserRepo, times(1)).findByEmailAndPassword(customer.getEmail(), customer.getPassword());
	}

	@Test
	@DisplayName("Test that createCustomer method calls BuildActualCustomer")
	void testTheCreateCustomerMethodCallsBuildActualCustomer() {
		lenient().when(userService.createCustomer(customerCreationRequest)).thenReturn(customer);
		Customer returnedCustomer = userService.createCustomer(customerCreationRequest);
		assertEquals(customer.getUsername(), returnedCustomer.getUsername());
		assertDoesNotThrow(()-> userService.createCustomer(customerCreationRequest));
	}

	@Test
	@DisplayName("Test that a customer's SIN is encoded")
	void testACustomersSINIsEncoded() {
		userService.createCustomer(customerCreationRequest);
		verify(mockPasswordEncoder, times(1)).encode(customerCreationRequest.getSin());
	}

	@Test
	@DisplayName("Test that createCustomer validates a customer password")
	void testThatCreateCustomerValidatesACustomerPassword(){
		userService.createCustomer(customerCreationRequest);
		verify(mockPasswordEncoder, times(1)).encode(customerCreationRequest.getPassword());
		assertTrue(customer.getPassword().contains("{bcrypt}$2a$10$"));
		assertDoesNotThrow(() -> userService.createCustomer(customerCreationRequest));
		assertEquals(LocalDateTime.of(2021,05,03,00,00,00,00),
				customer.getPasswordExpires());
	}

	@Test
	@DisplayName("Test that an invalid password throws IllegalArgumentException")
	void testThatAnInvalidPasswordThrowsIllegalArgumentException(){
		CustomerCreationDTO invalidCreationRequest;
		invalidCreationRequest = CustomerCreationDTO.builder()
				.username("Experiment626").title("Mr.").firstName("Stitch").lastName("Pelakai")
				.email("experiment626@gmail.com").phoneNumber("626-626-6266").password("1A2b3c45")
				.role("CUSTOMER").sin("626-626-626").industry("Entertainment")
				.occupation("Elvis Impersonator")
				.address(address)
				.account(accountCreationDTO)
				.build();

		Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.createCustomer(invalidCreationRequest));

		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(PASSWORD_ERROR_MESSAGE));
	}

	@Test
	@DisplayName("Test that a password less than 8 characters throws IllegalArgumentException")
	void testThatAPasswordLessThan8ThrowsIllegalArgumentException(){
		CustomerCreationDTO invalidCreationRequest;
		invalidCreationRequest = CustomerCreationDTO.builder()
				.username("Experiment626").title("Mr.").firstName("Stitch").lastName("Pelakai")
				.password("1A2b3")
				.address(address)
				.account(accountCreationDTO)
				.build();

		Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.createCustomer(invalidCreationRequest));

		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(PASSWORD_ERROR_MESSAGE));
	}

	@Test
	@DisplayName("Test that a password more than 20 characters throws IllegalArgumentException")
	void testThatAPasswordMoreThan20ThrowsIllegalArgumentException(){
		CustomerCreationDTO invalidCreationRequest;
		invalidCreationRequest = CustomerCreationDTO.builder()
				.username("Experiment626").title("Mr.").firstName("Stitch").lastName("Pelakai")
				.password("1A2b3c4!567890abcdefg")
				.address(address)
				.account(accountCreationDTO)
				.build();

		Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.createCustomer(invalidCreationRequest));

		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(PASSWORD_ERROR_MESSAGE));
	}

	@Test
	@DisplayName("Test that a Checking account is created when the CustomerCreationDTO specifies 'checking'")
	void testThatACheckingAccountIsCreatedWhenCustomerCreationDTOSpecifiesChecking(){
		customer.setMainAccount(checking);
		customer.addToOwnedAccounts(checking);

		userService.createCustomer(customerCreationRequest);
		assertTrue(customer.getMainAccount().getAccountType()
				.equals(AccountTypeEnum.valueOf(customerCreationRequest.getAccount().getAccountType())));
	}

	@Test
	@DisplayName("Test that a Savings account is created when the CustomerCreationDTO specifies 'savings'")
	void testThatASavingsAccountIsCreatedWhenCustomerCreationDTOSpecifiesSavings(){
		accountCreationDTO = AccountCreationDTO.builder()
				.openedByCustomerId(6L).accountType(AccountTypeEnum.SAVINGS.toString())
				.balance(BigDecimal.valueOf(550)).isMainAccount(true)
				.build();

		customerCreationRequest = CustomerCreationDTO.builder()
				.username("Experiment626").title("Mr.").firstName("Stitch").lastName("Pelakai")
				.email("experiment626@gmail.com").phoneNumber("626-626-6266").password("1A2b3c4!")
				.role("CUSTOMER").sin("626-626-626").industry("Entertainment")
				.occupation("Elvis Impersonator")
				.address(address)
				.account(accountCreationDTO)
				.build();

		customer.setMainAccount(savings);
		customer.addToOwnedAccounts(savings);

		userService.createCustomer(customerCreationRequest);
		assertTrue(customer.getMainAccount().getAccountType()
				.equals(AccountTypeEnum.valueOf(customerCreationRequest.getAccount().getAccountType())));
	}

	@Test
	@DisplayName("Test that a Student account is created when the CustomerCreationDTO specifies 'student'")
	void testThatAStudentAccountIsCreatedWhenCustomerCreationDTOSpecifiesStudent(){
		accountCreationDTO = AccountCreationDTO.builder()
				.openedByCustomerId(7L).accountType(AccountTypeEnum.STUDENT.toString())
				.balance(BigDecimal.valueOf(650)).isMainAccount(true)
				.build();

		customerCreationRequest = CustomerCreationDTO.builder()
				.username("Experiment626").title("Mr.").firstName("Stitch").lastName("Pelakai")
				.email("experiment626@gmail.com").phoneNumber("626-626-6266").password("1A2b3c4!")
				.role("CUSTOMER").sin("626-626-626").industry("Entertainment")
				.occupation("Elvis Impersonator")
				.address(address)
				.account(accountCreationDTO)
				.build();

		customer.setMainAccount(student);
		customer.addToOwnedAccounts(student);

		userService.createCustomer(customerCreationRequest);
		assertTrue(customer.getMainAccount().getAccountType()
				.equals(AccountTypeEnum.valueOf(customerCreationRequest.getAccount().getAccountType())));
	}

	@Test
	@DisplayName("Test that a First Class Checking account is created when the CustomerCreationDTO specifies 'firstClassChecking'")
	void testThatAFirstClassCheckingAccountIsCreatedWhenCustomerCreationDTOSpecifiesFirstClassChecking(){
		accountCreationDTO = AccountCreationDTO.builder()
				.openedByCustomerId(8L).accountType(AccountTypeEnum.FIRST_CLASS_CHECKING.toString())
				.balance(BigDecimal.valueOf(750)).isMainAccount(true)
				.build();

		customerCreationRequest = CustomerCreationDTO.builder()
				.username("Experiment626").title("Mr.").firstName("Stitch").lastName("Pelakai")
				.email("experiment626@gmail.com").phoneNumber("626-626-6266").password("1A2b3c4!")
				.role("CUSTOMER").sin("626-626-626").industry("Entertainment")
				.occupation("Elvis Impersonator")
				.address(address)
				.account(accountCreationDTO)
				.build();

		customer.setMainAccount(firstClassChecking);
		customer.addToOwnedAccounts(firstClassChecking);

		userService.createCustomer(customerCreationRequest);
		assertTrue(customer.getMainAccount().getAccountType()
				.equals(AccountTypeEnum.valueOf(customerCreationRequest.getAccount().getAccountType())));
	}

	@Test
	@DisplayName("Test that a Premium Visa account is created when the CustomerCreationDTO specifies 'premiumVisa'")
	void testThatAPremiumVisaAccountIsCreatedWhenCustomerCreationDTOSpecifiesPremiumVisa(){
		accountCreationDTO = AccountCreationDTO.builder()
				.openedByCustomerId(9L).accountType(AccountTypeEnum.PREMIUM_VISA.toString())
				.build();

		customerCreationRequest = CustomerCreationDTO.builder()
				.username("Experiment626").title("Mr.").firstName("Stitch").lastName("Pelakai")
				.email("experiment626@gmail.com").phoneNumber("626-626-6266").password("1A2b3c4!")
				.role("CUSTOMER").sin("626-626-626").industry("Entertainment")
				.occupation("Elvis Impersonator")
				.address(address)
				.account(accountCreationDTO)
				.build();

		customer.setMainAccount(premiumVisa);
		customer.addToOwnedAccounts(premiumVisa);

		userService.createCustomer(customerCreationRequest);
		assertTrue(customer.getMainAccount().getAccountType()
				.equals(AccountTypeEnum.valueOf(customerCreationRequest.getAccount().getAccountType())));
	}

	@Test
	@DisplayName("Test that a Business Visa account is created when the CustomerCreationDTO specifies 'businessVisa'")
	void testThatABusinessVisaAccountIsCreatedWhenCustomerCreationDTOSpecifiesBusinessVisa(){
		accountCreationDTO = AccountCreationDTO.builder()
				.openedByCustomerId(0L).accountType(AccountTypeEnum.BUSINESS_VISA.toString())
				.build();

		customerCreationRequest = CustomerCreationDTO.builder()
				.username("Experiment626").title("Mr.").firstName("Stitch").lastName("Pelakai")
				.email("experiment626@gmail.com").phoneNumber("626-626-6266").password("1A2b3c4!")
				.role("CUSTOMER").sin("626-626-626").industry("Entertainment")
				.occupation("Elvis Impersonator")
				.address(address)
				.account(accountCreationDTO)
				.build();

		customer.setMainAccount(businessVisa);
		customer.addToOwnedAccounts(businessVisa);

		userService.createCustomer(customerCreationRequest);
		assertTrue(customer.getMainAccount().getAccountType()
				.equals(AccountTypeEnum.valueOf(customerCreationRequest.getAccount().getAccountType())));
	}

	@Test
	@DisplayName("Test that an invalid AccountTypeEnum throws an IllegalArgumentException")
	void testThatAnInvalidAccountTypeEnumThrowsAnIllegalArgumentException(){
		AccountCreationDTO invalidAccountCreationDTO = AccountCreationDTO.builder()
				.isMainAccount(true).accountType("401K").openedByCustomerId(626L)
				.balance(BigDecimal.valueOf(500L)).build();

		customerCreationRequest = CustomerCreationDTO.builder()
				.username("Experiment626").title("Mr.").firstName("Stitch").lastName("Pelakai")
				.email("experiment626@gmail.com").phoneNumber("626-626-6266").password("1A2b3c4!")
				.role("CUSTOMER").sin("626-626-626").industry("Entertainment")
				.occupation("Elvis Impersonator")
				.address(address)
				.account(invalidAccountCreationDTO)
				.build();

		Exception exception = assertThrows(IllegalArgumentException.class, ()-> userService.createCustomer(customerCreationRequest));

		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains("Account Type 401K not found"));
	}

	@Test
	@DisplayName("Test that createCustomer method calls BuildAddress")
	void testTheCreateCustomerMethodCallsBuildAddress() {
		Customer returnedCustomer = userService.createCustomer(customerCreationRequest);
		assertEquals(customer.getAddressList().get(0).getPostalCode(),
				returnedCustomer.getAddressList().get(0).getPostalCode());
	}

	@Test
	@DisplayName("Test that createCustomer method calls BuildInitialTransaction")
	void testTheCreateCustomerMethodCallsBuildInitialTransaction(){
		checking.addTransaction(transaction);
		customer.setMainAccount(checking);
		customer.addToOwnedAccounts(checking);
		userService.createCustomer(customerCreationRequest);
		assertEquals(1, customer.getMainAccount().getAllTransactions().size());
	}

	@Test
	@DisplayName("Test that updateUser updates User email properly")
	void testThatUpdateUserUpdatesUserEmailProperly(){
		when(mockUserRepo.findById(626L)).thenReturn(Optional.ofNullable(customer));
		userService.updateUser(customer.getUserId(), userUpdate);
		assertEquals("cuteandfluffy@gmail.com", customer.getEmail());
	}

	@Test
	@DisplayName("Test that a blank field does not alter data")
	void testThatABlankFieldForUpdateDoesNotAlterData(){
		when(mockUserRepo.findById(626L)).thenReturn(Optional.ofNullable(customer));
		userService.updateUser(customer.getUserId(), userUpdate);
		assertEquals("cuteandfluffy@gmail.com", customer.getEmail());
		assertEquals("626-626-6266", customer.getPhoneNumber());
	}

	@Test
	@DisplayName("Test that updateUser updates other User information properly")
	void testThatUpdateUserUpdatesOtherUserInformationProperly(){
		userUpdate = UserUpdateDTO.builder()
				.title("Ms.")
				.email("updated@gmail.com")
				.phoneNumber("896-741-2350")
				.password("Updated123!")
				.occupation("Sales")
				.industry("Technology")
				.address(newAddress)
				.build();

		when(mockUserRepo.findById(626L)).thenReturn(Optional.ofNullable(customer));
		userService.updateUser(customer.getUserId(), userUpdate);
		assertEquals("updated@gmail.com", customer.getEmail());
		assertEquals("896-741-2350", customer.getPhoneNumber());
		assertEquals("Sales", customer.getOccupation());
		assertEquals("Technology", customer.getIndustry());
		assertEquals("New User Way", customer.getAddressList().get(0).getStreetName());
	}

	@Test
	@DisplayName("Test deleteUser updates the isActiveFlag")
	void testThatDeleteUserUpdatesTheIsActiveFlag(){
		when(mockUserRepo.findById(626L)).thenReturn(Optional.ofNullable(customer));
		userService.deleteUser(customer.getUserId());
		assertEquals(false, customer.getIsActive());
	}

}
