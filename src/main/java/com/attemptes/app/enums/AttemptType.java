package com.attemptes.app.enums;

public enum AttemptType {
    AUTH_WRONG_CREDENTIALS("auth:user", 5),
    DEVICE_NOT_ASSOCIATED("deviceToken", 3),
    REQUESTS_PER_SECOND("requestsPerSecond", 100),
    OTP_WRONG("otpWrong", 5),
    OTP_RESEND("otpResend", 3),
    REGISTRATION("registration", 10),
    PHONE_CHANGE("phoneChange", 3),
    DEVICE_ACTIVATION("deviceActivation", 5),
    ALIAS_CHANGE("aliasChange", 3);

    private final String keyPrefix;
    private final int maxAttempts;

    AttemptType(String keyPrefix, int maxAttempts) {
        this.keyPrefix = keyPrefix;
        this.maxAttempts = maxAttempts;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }
}
