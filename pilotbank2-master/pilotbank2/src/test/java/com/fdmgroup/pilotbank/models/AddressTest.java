package com.fdmgroup.pilotbank.models;

import com.fdmgroup.pilotbank2.models.Address;
import com.fdmgroup.pilotbank2.models.Customer;
import com.fdmgroup.pilotbank2.models.User;
import com.fdmgroup.pilotbank2.models.dto.CustomerCreationDTO;
import com.fdmgroup.pilotbank2.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class AddressTest {

    @Spy
    private UserServiceImpl mockUserService;

    private CustomerCreationDTO customerCreationDTO;
    private Customer customer;
    private Address address;
    private List<Address> addressList = new ArrayList<>();

    @BeforeEach
    void init() {
        address = Address.builder()
                .addressId(0L)
                .streetName("Test Ave.")
                .streetNumber("70-74")
                .suiteNumber("Unit 202A")
                .city("Test City")
                .province("ZZ")
                .postalCode("10A5B")
                .build();

        addressList.add(address);

        customerCreationDTO = CustomerCreationDTO.builder()
                .address(address)
                .build();

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
                .addressList(addressList)
                .build();

        address.setUser(customer);
    }

    @Test
    @DisplayName("Test if the address has the expected attributes")
    void test_if_address_has_expected_attributes() {
        doReturn(customer).when(mockUserService).createCustomer(customerCreationDTO);
        Customer customer1 = mockUserService.createCustomer(customerCreationDTO);
        assertEquals(0L, customer1.getAddressList().get(0).getAddressId());
        assertEquals("Test Ave.", customer1.getAddressList().get(0).getStreetName());
        assertEquals("70-74", customer1.getAddressList().get(0).getStreetNumber());
        assertEquals("Unit 202A", customer1.getAddressList().get(0).getSuiteNumber());
        assertEquals("Test City", customer1.getAddressList().get(0).getCity());
        assertEquals("ZZ", customer1.getAddressList().get(0).getProvince());
        assertEquals("10A5B", customer1.getAddressList().get(0).getPostalCode());
        assertEquals(customer, customer1.getAddressList().get(0).getUser());
    }

    @Test
    @DisplayName("Test if toString() returns the expected string")
    void test_if_toString_returns_expected_string() {
        doReturn(customer).when(mockUserService).createCustomer(customerCreationDTO);
        Customer customer1 = mockUserService.createCustomer(customerCreationDTO);
        String expectedStr = "Address [addressId=" + 0L + ", streetName=" + "Test Ave." + ", streetNumber=" + "70-74"
                + ", suiteNumber=" + "Unit 202A" + ", province=" + "ZZ" + ", city=" + "Test City" + ", postalCode="
                + "10A5B" + "]";
        assertEquals(expectedStr, customer1.getAddressList().get(0).toString());
    }
}
