package com.account.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Currency")
class CurrencyTest {

    @ParameterizedTest
    @ValueSource(strings = {"EUR", "eur", "SEK", "GBP", "USD"})
    void from_parsesAllowedCurrencies(String value) {
        assertThat(Currency.from(value)).isPresent();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"JPY", "BTC", " "})
    void from_rejectsInvalidCurrencies(String value) {
        assertThat(Currency.from(value)).isEmpty();
    }

    @Test
    void values_areSupportedCurrencies() {
        assertThat(Currency.values()).containsExactly(
                Currency.EUR, Currency.SEK, Currency.GBP, Currency.USD
        );
    }
}
