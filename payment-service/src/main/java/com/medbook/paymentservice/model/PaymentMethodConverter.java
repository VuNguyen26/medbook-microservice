package com.medbook.paymentservice.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {

    @Override
    public String convertToDatabaseColumn(PaymentMethod method) {
        return (method == null) ? null : method.name().toLowerCase();
    }

    @Override
    public PaymentMethod convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : PaymentMethod.valueOf(dbData.toUpperCase());
    }
}
