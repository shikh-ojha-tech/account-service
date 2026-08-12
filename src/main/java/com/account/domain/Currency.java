package com.account.domain;

import java.util.Arrays;
import java.util.Optional;

public enum Currency {
    EUR,
    SEK,
    GBP,
    USD;

    public static Optional<Currency> from(String value) {
        if (value == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(currency -> currency.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
