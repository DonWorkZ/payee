package com.fdmgroup.pilotbank2.common;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PilotBankConstants {
	public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,20}$";
	public static final String PASSWORD_ERROR_MESSAGE = "Password must contain 8-20 characters, at least one uppercase character," +
				" at least one lower case character, at least one digit, and at least one special symbol ( !@#$%^&-+= )," +
				" spaces are not allowed.";
	public static int CURRENT_DAY =  LocalDateTime.now().getDayOfMonth();
	public static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	public static final int CHECKING_MONTHLY_TRANSACTION_LIMIT = 25;
	public static final BigDecimal CHECKING_TRANSACTION_FEE = BigDecimal.valueOf(1.50);
	public static final BigDecimal CHECKING_MONTHLY_FEE = BigDecimal.valueOf(10.95);
	public static final BigDecimal CHECKING_MINIMUM_BALANCE = BigDecimal.valueOf(3_500.00);
	public static final BigDecimal CHECKING_E_TRANSFER_FEE = BigDecimal.valueOf(0.50);
	public static final BigDecimal CHECKING_E_TRANSFER_CHARGE_BASE_AMOUNT = BigDecimal.valueOf(100.00);
	public static final BigDecimal FIRST_CLASS_CHECKING_MONTHLY_FEE = BigDecimal.valueOf(35.00);
	public static final BigDecimal FIRST_CLASS_CHECKING_MINIMUM_BALANCE = BigDecimal.valueOf(5_000.00);
	public static final BigDecimal SAVINGS_INTEREST_RATE = BigDecimal.valueOf(0.075);
	public static final BigDecimal SAVINGS_MINIMUM_BALANCE = BigDecimal.valueOf(0.00);
	public static final BigDecimal BUSINESS_VISA_CASH_BACK_RATE = BigDecimal.valueOf(0.01);
	public static final BigDecimal BUSINESS_VISA_INTEREST_RATE = BigDecimal.valueOf(0.21);
	public static final BigDecimal BUSINESS_VISA_CREDIT_LIMIT = BigDecimal.valueOf(30_000.00);
	public static final BigDecimal BUSINESS_VISA_ANNUAL_FEE = BigDecimal.valueOf(130.00);
	public static final BigDecimal PREMIUM_VISA_CASH_BACK_RATE = BigDecimal.valueOf(0.005);
	public static final BigDecimal PREMIUM_VISA_INTEREST_RATE = BigDecimal.valueOf(0.23);
	public static final BigDecimal PREMIUM_VISA_CREDIT_LIMIT = BigDecimal.valueOf(5_000.00);
	public static final BigDecimal STUDENT_MONTHLY_FEE = BigDecimal.valueOf(4.00);
}
