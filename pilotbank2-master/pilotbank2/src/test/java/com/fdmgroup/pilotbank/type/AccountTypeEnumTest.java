package com.fdmgroup.pilotbank.type;

import com.fdmgroup.pilotbank2.type.AccountTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTypeEnumTest {

    @Test
    @DisplayName("Test that accountTypeName returns an expected string")
    void test_accountTypeName_returns_expected_string() {
        String enumStr = AccountTypeEnum.PREMIUM_VISA.getAccountTypeName();
        assertEquals("Premium Visa", enumStr);
    }
}
