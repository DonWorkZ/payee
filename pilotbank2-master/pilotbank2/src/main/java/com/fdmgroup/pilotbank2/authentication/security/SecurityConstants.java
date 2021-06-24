package com.fdmgroup.pilotbank2.authentication.security;

public class SecurityConstants {

    public static final String SECRET = "P|L073@\\<i$0u7of=L1204=L350!"; //Pilot Bank is (climbing) out of 12,000 feet for 35,000 feet.
    public static final long EXPIRATION_TIME = 900_000; // 15 minutes
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String SIGN_UP_URL = "/users/customers/create";
    public static final String LOGIN_URL = "/auth/login";
    public static final String DEVICE_AUTHORIZATION_URL = "/auth/authorizeDevice";
    public static final String TWO_STEP_VERIFICATION_INITIATION_URL = "/auth/initiateTwoStepVerification";
    public static final String FORGOT_PW_URL = "/auth/forgotPassword";
    public static final String SECURITY_ANSWER_URL = "/auth/answerSecurityQuestion";
    public static final String REQUEST_SECURITY_CODE_URL = "/auth/requestSecurityCode";
    public static final String VERIFY_SECURITY_CODE_URL = "/auth/verifySecurityCode";
    public static final String UPDATE_PW_URL = "/auth/updatePassword";
}
