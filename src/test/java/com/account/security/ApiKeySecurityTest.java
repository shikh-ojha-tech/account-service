package com.account.security;

import com.account.api.AccountController;
import com.account.api.error.ApiExceptionHandler;
import com.account.config.SecurityConfig;
import com.account.domain.Account;
import com.account.exception.ErrorCodes;
import com.account.service.AccountDetails;
import com.account.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
@Import({SecurityConfig.class, ApiKeyAuthFilter.class, JsonUnauthorizedEntryPoint.class, ApiExceptionHandler.class})
@TestPropertySource(properties = "app.security.api-key=test-api-key")
@DisplayName("API key security")
class ApiKeySecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void rejectsMissingApiKey() throws Exception {
        mockMvc.perform(get("/accounts/{accountId}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCodes.UNAUTHORIZED));
    }

    @Test
    void acceptsValidApiKey() throws Exception {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setAccountId(accountId);
        account.setCustomerId("cust");
        when(accountService.getAccount(accountId)).thenReturn(new AccountDetails(account, List.of()));

        mockMvc.perform(get("/accounts/{accountId}", accountId)
                        .header(ApiKeyAuthFilter.API_KEY_HEADER, "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId.toString()));
    }
}
