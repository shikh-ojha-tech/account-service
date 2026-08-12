package com.account.domain;

import java.util.Arrays;
import java.util.Optional;

public enum Direction {
    IN,
    OUT;

    public static Optional<Direction> from(String value) {
        if (value == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(direction -> direction.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
