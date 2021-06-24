package com.fdmgroup.pilotbank.models;

import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.Savings;
import com.fdmgroup.pilotbank2.models.Student;
import com.fdmgroup.pilotbank2.models.dto.AccountCreationDTO;
import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.fdmgroup.pilotbank2.common.PilotBankConstants.*;
import static com.fdmgroup.pilotbank2.type.TransactionMemoEnum.MINIMUM_BALANCE_CHARGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class StudentTest {

    private Customer customer;
    private AccountCreationDTO accountCreationRequest;
    private Student student;

    @BeforeEach
    void init() {
        customer = Customer.builder()
                .userId(100L)
                .username("UnitTest")
                .title("Mr.")
                .firstName("Unit")
                .lastName("Test")
                .email("unit.test@email.com")
                .phoneNumber("111-111-1111")
                .password("1A2b3c4!")
                .role("CUSTOMER")
                .sin("111-111-999")
                .industry("Energy")
                .occupation("Sales")
                .build();

        accountCreationRequest = AccountCreationDTO.builder()
                .openedByCustomerId(100L)
                .accountType(AccountTypeEnum.STUDENT.toString())
                .balance(BigDecimal.valueOf(100))
                .isMainAccount(true)
                .build();

        student = Student.builder()
                .accountId(1000L)
                .accountType(AccountTypeEnum.valueOf(accountCreationRequest.getAccountType()))
                .ownedAccountCustomer(customer)
                .balance(accountCreationRequest.getBalance())
                .isEligibleStudent(true)
                .monthlyServiceFee(STUDENT_MONTHLY_FEE)
                .build();
    }

    @Test
    @DisplayName("Test if the student account has the expected attributes")
    void test_if_student_account_has_expected_attributes() {
        assertEquals(true, student.getIsEligibleStudent());
        assertEquals(STUDENT_MONTHLY_FEE, student.getMonthlyServiceFee());
    }

    @Test
    @DisplayName("Test if the no arg constructor creates a student account with default values")
    void test_if_no_arg_constructor_creates_student_account_with_default_values() {
        Student defaultStudent = new Student();
        assertEquals(true, defaultStudent.getIsEligibleStudent());
        assertEquals(STUDENT_MONTHLY_FEE, defaultStudent.getMonthlyServiceFee());
    }

    @Test
    @DisplayName("Test if the all arg constructor creates a student account with specified values")
    void test_if_all_arg_constructor_creates_student_account_with_specified_values() {
        Student allArgStudent = new Student(false, BigDecimal.valueOf(99));
        assertEquals(false, allArgStudent.getIsEligibleStudent());
        assertEquals(BigDecimal.valueOf(99), allArgStudent.getMonthlyServiceFee());
    }

    @Test
    @DisplayName("Test if the setter updates an attribute value")
    void test_if_setter_updates_attribute_value() {
        assertEquals(true, student.getIsEligibleStudent());
        student.setIsEligibleStudent(false);
        assertFalse(student.getIsEligibleStudent());
    }

    @Test
    @DisplayName("Test if toString() returns the expected string")
    void test_if_toString_returns_expected_string() {
        String expectedStr = "Account [accountId=" + 1000L + ", balance=" + BigDecimal.valueOf(100)
                + ", accountCreationDate=" + null + ", accountType=" + AccountTypeEnum.STUDENT.toString()
                + "]" + " Student [" + "isEligibleStudent=" + true + ", monthlyServiceFee=" + STUDENT_MONTHLY_FEE + "]";
        assertEquals(expectedStr, student.toString());
    }
}
