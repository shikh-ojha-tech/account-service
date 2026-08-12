package com.account.api;

import com.account.api.dto.AccountResponse;
import com.account.api.dto.BalanceDto;
import com.account.api.dto.CreateAccountRequest;
import com.account.domain.Balance;
import com.account.service.AccountDetails;
import com.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Create account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountDetails details = accountService.createAccount(
                request.getCustomerId(),
                request.getCountry(),
                request.getCurrencies()
        );
        return toResponse(details);
    }

    @Operation(summary = "Get account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@PathVariable UUID accountId) {
        return toResponse(accountService.getAccount(accountId));
    }

    private static AccountResponse toResponse(AccountDetails details) {
        List<BalanceDto> balances = details.getBalances().stream()
                .map(AccountController::toBalanceDto)
                .toList();
        return new AccountResponse(
                details.getAccount().getAccountId(),
                details.getAccount().getCustomerId(),
                balances
        );
    }

    private static BalanceDto toBalanceDto(Balance balance) {
        return new BalanceDto(balance.getAvailableAmount(), balance.getCurrency().name());
    }
}
