package com.account.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateAccountRequest {

    @NotBlank
    @Size(max = 64)
    private String customerId;

    @NotBlank
    @Size(min = 2, max = 2)
    private String country;

    @NotEmpty
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
