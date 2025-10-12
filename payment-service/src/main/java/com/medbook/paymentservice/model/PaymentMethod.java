package com.medbook.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    CASH("cash"),
    CREDIT_CARD("credit_card"),
    MOMO("momo"),
    INSURANCE("insurance");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PaymentMethod fromValue(String value) {
        for (PaymentMethod method : values()) {
            if (method.value.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment method: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
