package com.account.integration;

import com.jayway.jsonpath.JsonPath;
import com.account.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class TransactionApiIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createInTransaction_increasesBalance() throws Exception {
        String accountId = createAccount("txn-customer-1", "EUR", "USD");

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "%s",
                                  "amount": 100.50,
                                  "currency": "EUR",
                                  "direction": "IN",
                                  "description": "Salary"
                                }
                                """.formatted(accountId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.transactionId").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(100.50))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.direction").value("IN"))
                .andExpect(jsonPath("$.description").value("Salary"))
                .andExpect(jsonPath("$.balanceAfterTransaction").value(100.50));

        mockMvc.perform(get("/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balances[0].currency").value("EUR"))
                .andExpect(jsonPath("$.balances[0].availableAmount").value(100.50));
    }

    @Test
    void createOutTransaction_decreasesBalance() throws Exception {
        String accountId = createAccount("txn-customer-2", "EUR");
        createTransaction(accountId, "50.00", "EUR", "IN", "Top up");

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "%s",
                                  "amount": 20.00,
                                  "currency": "EUR",
                                  "direction": "OUT",
                                  "description": "Purchase"
                                }
                                """.formatted(accountId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.direction").value("OUT"))
                .andExpect(jsonPath("$.balanceAfterTransaction").value(30.00));
    }

    @Test
    void createOutTransaction_rejectsInsufficientFunds() throws Exception {
        String accountId = createAccount("txn-customer-3", "EUR");

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "%s",
                                  "amount": 10.00,
                                  "currency": "EUR",
                                  "direction": "OUT",
                                  "description": "Too much"
                                }
                                """.formatted(accountId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_FUNDS"));
    }

    @Test
    void createTransaction_rejectsMissingDescription() throws Exception {
        String accountId = createAccount("txn-customer-4", "EUR");

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "%s",
                                  "amount": 10.00,
                                  "currency": "EUR",
                                  "direction": "IN",
                                  "description": " "
                                }
                                """.formatted(accountId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DESCRIPTION_MISSING"));
    }

    @Test
    void createTransaction_rejectsInvalidDirection() throws Exception {
        String accountId = createAccount("txn-customer-5", "EUR");

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "%s",
                                  "amount": 10.00,
                                  "currency": "EUR",
                                  "direction": "SIDEWAYS",
                                  "description": "Nope"
                                }
                                """.formatted(accountId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_DIRECTION"));
    }

    @Test
    void createTransaction_rejectsInvalidAmount() throws Exception {
        String accountId = createAccount("txn-customer-6", "EUR");

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "%s",
                                  "amount": -5,
                                  "currency": "EUR",
                                  "direction": "IN",
                                  "description": "Bad amount"
                                }
                                """.formatted(accountId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_AMOUNT"));
    }

    @Test
    void createTransaction_rejectsMissingAccount() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "22222222-2222-2222-2222-222222222222",
                                  "amount": 10.00,
                                  "currency": "EUR",
                                  "direction": "IN",
                                  "description": "Unknown account"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }

    @Test
    void createTransaction_rejectsCurrencyNotOnAccount() throws Exception {
        String accountId = createAccount("txn-customer-7", "EUR");

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "%s",
                                  "amount": 10.00,
                                  "currency": "USD",
                                  "direction": "IN",
                                  "description": "Wrong currency"
                                }
                                """.formatted(accountId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_CURRENCY"));
    }

    @Test
    void getTransactions_returnsHistory() throws Exception {
        String accountId = createAccount("txn-customer-8", "EUR");
        createTransaction(accountId, "40.00", "EUR", "IN", "First");
        createTransaction(accountId, "10.00", "EUR", "OUT", "Second");

        mockMvc.perform(get("/accounts/{accountId}/transactions", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description").value("Second"))
                .andExpect(jsonPath("$[1].description").value("First"));
    }

    @Test
    void getTransactions_rejectsUnknownAccount() throws Exception {
        mockMvc.perform(get("/accounts/{accountId}/transactions", "33333333-3333-3333-3333-333333333333"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }

    private String createAccount(String customerId, String... currencies) throws Exception {
        String currencyJson = "\"" + String.join("\",\"", currencies) + "\"";
        MvcResult result = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "%s",
                                  "country": "EE",
                                  "currencies": [%s]
                                }
                                """.formatted(customerId, currencyJson)))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.accountId");
    }

    private void createTransaction(String accountId,
                                   String amount,
                                   String currency,
                                   String direction,
                                   String description) throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "%s",
                                  "amount": %s,
                                  "currency": "%s",
                                  "direction": "%s",
                                  "description": "%s"
                                }
                                """.formatted(accountId, amount, currency, direction, description)))
                .andExpect(status().isCreated());
    }
}
