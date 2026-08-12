package com.account.api.error;

import com.account.exception.DomainException;
import com.account.exception.ErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiExceptionHandler")
class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void mapsAccountNotFoundTo404() {
        ResponseEntity<ErrorResponse> response = handler.handleDomainException(
                new DomainException(ErrorCodes.ACCOUNT_NOT_FOUND, "Account not found")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.ACCOUNT_NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Account not found");
    }

    @Test
    void mapsBusinessErrorsTo400() {
        ResponseEntity<ErrorResponse> response = handler.handleDomainException(
                new DomainException(ErrorCodes.INSUFFICIENT_FUNDS, "Insufficient funds")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INSUFFICIENT_FUNDS);
    }

    @Test
    void mapsBadRequestExceptionsTo400() {
        ResponseEntity<ErrorResponse> response = handler.handleBadRequest(
                new IllegalArgumentException("bad json")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INVALID_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("bad json");
    }

    @Test
    void mapsValidationErrorsTo400() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "customerId", "size must be between 0 and 64"));
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INVALID_REQUEST);
        assertThat(response.getBody().getMessage()).contains("customerId");
    }

    @Test
    void mapsDataIntegrityViolationTo400() {
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrity(
                new DataIntegrityViolationException("value too long")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INVALID_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Request violates data constraints");
    }
}
