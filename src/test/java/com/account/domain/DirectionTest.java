package com.account.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Direction")
class DirectionTest {

    @ParameterizedTest
    @ValueSource(strings = {"IN", "in", "OUT", "out"})
    void from_parsesAllowedDirections(String value) {
        assertThat(Direction.from(value)).isPresent();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"SIDEWAYS", "TRANSFER"})
    void from_rejectsInvalidDirections(String value) {
        assertThat(Direction.from(value)).isEmpty();
    }

    @Test
    void values_areInAndOut() {
        assertThat(Direction.values()).containsExactly(Direction.IN, Direction.OUT);
    }
}
