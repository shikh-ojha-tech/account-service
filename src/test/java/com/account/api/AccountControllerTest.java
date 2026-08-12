package com.account.api;

import com.account.api.error.ApiExceptionHandler;
import com.account.domain.Account;
import com.account.domain.Balance;
import com.account.domain.Currency;
import com.account.exception.DomainException;
import com.account.exception.ErrorCodes;
import com.account.service.AccountDetails;
import com.account.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
@DisplayName("AccountController")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void createAccount_returnsCreatedResponse() throws Exception {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setAccountId(accountId);
        account.setCustomerId("cust-1");
        account.setCountry("EE");

        Balance balance = new Balance();
        balance.setCurrency(Currency.EUR);
        balance.setAvailableAmount(BigDecimal.ZERO.setScale(2));

        when(accountService.createAccount(eq("cust-1"), eq("EE"), anyList()))
                .thenReturn(new AccountDetails(account, List.of(balance)));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "cust-1",
                                  "country": "EE",
                                  "currencies": ["EUR"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.customerId").value("cust-1"))
                .andExpect(jsonPath("$.balances[0].currency").value("EUR"));
    }

    @Test
    void getAccount_returnsAccount() throws Exception {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setAccountId(accountId);
        account.setCustomerId("cust-2");

        when(accountService.getAccount(accountId))
                .thenReturn(new AccountDetails(account, List.of()));

        mockMvc.perform(get("/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.customerId").value("cust-2"));
    }

    @Test
    void getAccount_mapsNotFound() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(accountService.getAccount(accountId))
                .thenThrow(new DomainException(ErrorCodes.ACCOUNT_NOT_FOUND, "Account not found"));

        mockMvc.perform(get("/accounts/{accountId}", accountId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCodes.ACCOUNT_NOT_FOUND));
    }

    @Test
    void createAccount_mapsInvalidCurrency() throws Exception {
        when(accountService.createAccount(anyString(), anyString(), anyList()))
                .thenThrow(new DomainException(ErrorCodes.INVALID_CURRENCY, "Invalid currency"));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "cust-1",
                                  "country": "EE",
                                  "currencies": ["JPY"]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCodes.INVALID_CURRENCY));
    }

    @Test
    void createAccount_rejectsOversizedCustomerId() throws Exception {
        String tooLong = "c".repeat(65);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "%s",
                                  "country": "EE",
                                  "currencies": ["EUR"]
                                }
                                """.formatted(tooLong)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCodes.INVALID_REQUEST));
    }

    @Test
    void createAccount_rejectsInvalidCountryLength() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "cust-1",
                                  "country": "EST",
                                  "currencies": ["EUR"]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCodes.INVALID_REQUEST));
    }
}
