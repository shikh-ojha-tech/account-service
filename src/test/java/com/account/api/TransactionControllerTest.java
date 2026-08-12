package com.account.api;

import com.account.api.error.ApiExceptionHandler;
import com.account.domain.Currency;
import com.account.domain.Direction;
import com.account.domain.Transaction;
import com.account.exception.DomainException;
import com.account.exception.ErrorCodes;
import com.account.service.TransactionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
@DisplayName("TransactionController")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    void createTransaction_returnsCreatedResponse() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionId(transactionId);
        transaction.setAmount(new BigDecimal("10.00"));
        transaction.setCurrency(Currency.EUR);
        transaction.setDirection(Direction.IN);
        transaction.setDescription("Salary");
        transaction.setBalanceAfterTransaction(new BigDecimal("10.00"));

        when(transactionService.createTransaction(
                eq(accountId), any(), eq("EUR"), eq("IN"), eq("Salary")
        )).thenReturn(transaction);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "%s",
                                  "amount": 10.00,
                                  "currency": "EUR",
                                  "direction": "IN",
                                  "description": "Salary"
                                }
                                """.formatted(accountId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(transactionId.toString()))
                .andExpect(jsonPath("$.balanceAfterTransaction").value(10.00));
    }

    @Test
    void getTransactions_returnsListWithoutBalanceAfter() throws Exception {
        UUID accountId = UUID.randomUUID();
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setAmount(new BigDecimal("5.00"));
        transaction.setCurrency(Currency.USD);
        transaction.setDirection(Direction.OUT);
        transaction.setDescription("Buy");
        transaction.setBalanceAfterTransaction(new BigDecimal("1.00"));

        when(transactionService.getTransactions(accountId)).thenReturn(List.of(transaction));

        mockMvc.perform(get("/accounts/{accountId}/transactions", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Buy"))
                .andExpect(jsonPath("$[0].balanceAfterTransaction").doesNotExist());
    }

    @Test
    void createTransaction_mapsInsufficientFunds() throws Exception {
        when(transactionService.createTransaction(any(), any(), anyString(), anyString(), anyString()))
                .thenThrow(new DomainException(ErrorCodes.INSUFFICIENT_FUNDS, "Insufficient funds"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "11111111-1111-1111-1111-111111111111",
                                  "amount": 10.00,
                                  "currency": "EUR",
                                  "direction": "OUT",
                                  "description": "X"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCodes.INSUFFICIENT_FUNDS));
    }

    @Test
    void createTransaction_rejectsOversizedDescription() throws Exception {
        String tooLong = "d".repeat(256);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "11111111-1111-1111-1111-111111111111",
                                  "amount": 10.00,
                                  "currency": "EUR",
                                  "direction": "IN",
                                  "description": "%s"
                                }
                                """.formatted(tooLong)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCodes.INVALID_REQUEST));
    }
}
