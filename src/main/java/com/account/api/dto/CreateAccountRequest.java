package com.account.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Open a new account")
public class CreateAccountRequest {

    @NotBlank
    @Size(max = 64)
    @Schema(description = "Who owns the account", example = "cust-1")
    private String customerId;

    @NotBlank
    @Size(min = 2, max = 2)
    @Schema(description = "Country code, 2 letters", example = "EE")
    private String country;

    @NotEmpty
    @Schema(description = "Currencies to open (EUR, SEK, GBP, USD)", example = "[\"EUR\",\"USD\"]")
    private List<@NotBlank @Size(max = 3) String> currencies;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies;
    }
}
