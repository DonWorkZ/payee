package com.fdmgroup.pilotbank.controllers;

import com.fdmgroup.pilotbank2.controllers.PayeeController;
import com.fdmgroup.pilotbank2.models.Payee;
import com.fdmgroup.pilotbank2.models.dto.PayeeCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.PayeeUpdateDTO;
import com.fdmgroup.pilotbank2.services.PayeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class PayeeControllerTest {

    @InjectMocks
    private PayeeController payeeController;

    @Mock
    private PayeeService mockPayeeService;

    private Payee payee1, payee2;
    private PayeeCreationDTO payeeCreationDTO = new PayeeCreationDTO();
    private List<Payee> payees = new ArrayList<>();
    private PayeeUpdateDTO payeeUpdateDTO = new PayeeUpdateDTO();

    @BeforeEach
    void init() {
        payeeCreationDTO = PayeeCreationDTO.builder()
                .companyName("TestPayee")
                .postalCode("98765")
                .accountNumber("147852369")
                .nickname("TestNick")
                .build();

        payee1 = Payee.builder()
                .companyName(payeeCreationDTO.getCompanyName())
                .accountNumber(payeeCreationDTO.getAccountNumber())
                .build();

        payeeCreationDTO = PayeeCreationDTO.builder()
                .companyName("TestPayee2")
                .postalCode("22222")
                .accountNumber("222222222")
                .nickname("TestNick2")
                .build();

        payee2 = Payee.builder()
                .companyName(payeeCreationDTO.getCompanyName())
                .accountNumber(payeeCreationDTO.getAccountNumber())
                .build();

        payees.add(payee1);
        payees.add(payee2);

        payeeCreationDTO = PayeeCreationDTO.builder()
                .companyName("TestPayee3")
                .postalCode("33333")
                .accountNumber("3333333333")
                .nickname("TestNick3")
                .build();

        payeeUpdateDTO = PayeeUpdateDTO.builder()
                            .companyName("UpdatedPayee")
                            .postalCode("UpdatedPostal")
                            .accountNumber("UpdatedAccNum")
                            .nickname("UpdatedNick")
                            .build();
    }

    @Test
    @DisplayName("test that findAllPayees returns a payee list")
    void test_findAllPayees_returns_account_list() {
        when(mockPayeeService.findAllPayees()).thenReturn(payees);

        ResponseEntity<?> payeeList = payeeController.findAllPayees();
        verify(mockPayeeService, times(1)).findAllPayees();
        assertEquals(2, ((List<Payee>)payeeList.getBody()).size());
        assertEquals(payee1, ((List<Payee>)payeeList.getBody()).get(0));
        assertEquals(payee2, ((List<Payee>)payeeList.getBody()).get(1));
        assertEquals(HttpStatus.OK, payeeList.getStatusCode());
    }

    @Test
    @DisplayName("Test that createPayee returns a new payee")
    void test_createPayee_returns_newPayee() {
        Payee payee3 = Payee.builder()
                .companyName(payeeCreationDTO.getCompanyName())
                .accountNumber(payeeCreationDTO.getAccountNumber())
                .build();

        when(mockPayeeService.createPayee(payeeCreationDTO)).thenReturn(payee3);
        ResponseEntity<?> payee = payeeController.createPayee(payeeCreationDTO);
        verify(mockPayeeService, times(1)).createPayee(payeeCreationDTO);
        assertEquals(payee3, payee.getBody());
        assertEquals(HttpStatus.CREATED, payee.getStatusCode());
    }

    @Test
    @DisplayName("Test that createPayee throws exception")
    void test_createPayee_throws_Exception() {
        when(mockPayeeService.createPayee(payeeCreationDTO))
                .thenThrow(new IllegalStateException("IllegalStateException") {});
        ResponseEntity<?> payee = payeeController.createPayee(payeeCreationDTO);

        String expectedMessage = String.format("Error creating Payee: %s",
                "com.fdmgroup.pilotbank.controllers.PayeeControllerTest$1: IllegalStateException");
        assertEquals(expectedMessage, payee.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, payee.getStatusCode());
    }

    @Test
    @DisplayName("Test that findByPayeeId returns a payee")
    void test_findByPayeeId_returns_Payee() {
        when(mockPayeeService.findByPayeeId(payee2.getPayeeId())).thenReturn(payee2);
        ResponseEntity<?> payee = payeeController.findByPayeeId(payee2.getPayeeId());
        verify(mockPayeeService, times(1)).findByPayeeId(payee2.getPayeeId());
        assertEquals(payee2, payee.getBody());
        assertEquals(HttpStatus.OK, payee.getStatusCode());
    }

    @Test
    @DisplayName("Test that findByPayeeId throws exception")
    void test_findByPayeeId_throws_Exception() {
        when(mockPayeeService.findByPayeeId(payee2.getPayeeId()))
                .thenThrow(new IllegalStateException("IllegalStateException") {});
        ResponseEntity<?> payee = payeeController.findByPayeeId(payee2.getPayeeId());

        String expectedMessage = String.format("Error processing Payee: %s",
                "com.fdmgroup.pilotbank.controllers.PayeeControllerTest$2: IllegalStateException");
        assertEquals(expectedMessage, payee.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, payee.getStatusCode());
    }

    @Test
    @DisplayName("Test that updatePayee returns a payee")
    void test_updatePayee_returns_Payee() {
        payee1 = Payee.builder()
                .companyName(payeeUpdateDTO.getCompanyName())
                .accountNumber(payeeUpdateDTO.getAccountNumber())
                .postalCode(payeeUpdateDTO.getPostalCode())
                .nickname(payeeUpdateDTO.getNickname())
                .build();

        when(mockPayeeService.updatePayee(payee1.getPayeeId(), payeeUpdateDTO)).thenReturn(payee1);
        ResponseEntity<?> payee = payeeController.updatePayee(payee1.getPayeeId(), payeeUpdateDTO);
        verify(mockPayeeService, times(1)).updatePayee(payee1.getPayeeId(), payeeUpdateDTO);
        assertEquals(payee1, payee.getBody());
        assertEquals(HttpStatus.OK, payee.getStatusCode());
    }

    @Test
    @DisplayName("Test that updatePayee throws exception")
    void test_updatePayee_throws_Exception() {
        when(mockPayeeService.updatePayee(payee1.getPayeeId(), payeeUpdateDTO))
                .thenThrow(new IllegalStateException("IllegalStateException") {});
        ResponseEntity<?> payee = payeeController.updatePayee(payee1.getPayeeId(), payeeUpdateDTO);

        String expectedMessage = String.format("Unable to update Payee: %s",
                "com.fdmgroup.pilotbank.controllers.PayeeControllerTest$3: IllegalStateException");
        assertEquals(expectedMessage, payee.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, payee.getStatusCode());
    }

    @Test
    @DisplayName("Test that deletePayee returns a success message")
    void test_deletePayee_returns_successMessage() {
        ResponseEntity<?> payee = payeeController.deletePayee(payee2.getPayeeId());
        String expectedMessage = String.format("Payee ID: %s, deleted successfully", payee2.getPayeeId());

        verify(mockPayeeService, times(1)).deletePayee(payee2.getPayeeId());
        assertEquals(expectedMessage, payee.getBody());
        assertEquals(HttpStatus.OK, payee.getStatusCode());
    }

    @Test
    @DisplayName("Test that deletePayee throws exception")
    void test_deletePayee_throws_Exception() {
        doThrow(new IllegalStateException("IllegalStateException") {}).when(mockPayeeService).deletePayee(payee2.getPayeeId());
        ResponseEntity<?> payee = payeeController.deletePayee(payee2.getPayeeId());

        String expectedMessage = String.format("Error deleting payee: %s",
                "com.fdmgroup.pilotbank.controllers.PayeeControllerTest$4: IllegalStateException");
        assertEquals(expectedMessage, payee.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, payee.getStatusCode());
    }
}
