package com.account.api;

import com.account.api.dto.CreateTransactionRequest;
import com.account.api.dto.CreateTransactionResponse;
import com.account.api.dto.TransactionResponse;
import com.account.domain.Transaction;
import com.account.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Create transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = CreateTransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PostMapping("/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateTransactionResponse createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        Transaction transaction = transactionService.createTransaction(
                request.getAccountId(),
                request.getAmount(),
                request.getCurrency(),
                request.getDirection(),
                request.getDescription()
        );
        return toCreateResponse(transaction);
    }

    @Operation(summary = "List transactions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransactionResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/accounts/{accountId}/transactions")
    public List<TransactionResponse> getTransactions(
            @Parameter(description = "Account to list history for") @PathVariable UUID accountId) {
        return transactionService.getTransactions(accountId).stream()
                .map(TransactionController::toHistoryResponse)
                .toList();
    }

    private static CreateTransactionResponse toCreateResponse(Transaction transaction) {
        CreateTransactionResponse response = new CreateTransactionResponse();
        response.setAccountId(transaction.getAccountId());
        response.setTransactionId(transaction.getTransactionId());
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency().name());
        response.setDirection(transaction.getDirection().name());
        response.setDescription(transaction.getDescription());
        response.setBalanceAfterTransaction(transaction.getBalanceAfterTransaction());
        return response;
    }

    private static TransactionResponse toHistoryResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setAccountId(transaction.getAccountId());
        response.setTransactionId(transaction.getTransactionId());
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency().name());
        response.setDirection(transaction.getDirection().name());
        response.setDescription(transaction.getDescription());
        return response;
    }
}
