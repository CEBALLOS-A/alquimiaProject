package com.alchemy.operation.infrastructure.adapter.input.rest;

import com.alchemy.operation.application.dto.ErrorResponse;
import com.alchemy.operation.domain.exception.DomainException;
import com.alchemy.operation.domain.exception.LocationCalculationException;
import com.alchemy.operation.domain.exception.OperationNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesDomainExceptionAsBadRequest() {
        ResponseEntity<ErrorResponse> response = handler.handleDomainException(
                new DomainException("Invalid device"), request("/api/operations/devices"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertErrorResponse(response.getBody(), HttpStatus.BAD_REQUEST, "Invalid device", "/api/operations/devices");
    }

    @Test
    void handlesOperationNotFoundExceptionAsNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleOperationNotFoundException(
                new OperationNotFoundException("Operation not found"), request("/api/operations/1"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertErrorResponse(response.getBody(), HttpStatus.NOT_FOUND, "Operation not found", "/api/operations/1");
    }

    @Test
    void handlesLocationCalculationExceptionAsUnprocessableEntity() {
        ResponseEntity<ErrorResponse> response = handler.handleLocationCalculationException(
                new LocationCalculationException("Distances do not define a common laboratory location"),
                request("/api/operations/process"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertErrorResponse(response.getBody(), HttpStatus.UNPROCESSABLE_ENTITY,
                "Distances do not define a common laboratory location", "/api/operations/process");
    }

    @Test
    void handlesIllegalArgumentExceptionAsBadRequest() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(
                new IllegalArgumentException("Invalid distance"), request("/api/operations/devices"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertErrorResponse(response.getBody(), HttpStatus.BAD_REQUEST, "Invalid distance", "/api/operations/devices");
    }

    @Test
    void handlesValidationExceptionAsUnprocessableEntity() throws Exception {
        Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummy", String.class);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "distance", "must be positive"));
        bindingResult.addError(new FieldError("request", "deviceType", "must not be null"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                new MethodParameter(method, 0), bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(
                exception, request("/api/operations/devices"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertErrorResponse(response.getBody(), HttpStatus.UNPROCESSABLE_ENTITY,
                "deviceType: must not be null, distance: must be positive", "/api/operations/devices");
    }

    @Test
    void handlesUnexpectedExceptionAsInternalServerError() {
        ResponseEntity<ErrorResponse> response = handler.handleUnexpectedException(
                new IllegalStateException("internal detail"), request("/api/operations/process"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertErrorResponse(response.getBody(), HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred", "/api/operations/process");
    }

    private MockHttpServletRequest request(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(path);
        return request;
    }

    private void assertErrorResponse(ErrorResponse body, HttpStatus status, String message, String path) {
        assertNotNull(body);
        assertNotNull(body.getTimestamp());
        assertEquals(status.value(), body.getStatus());
        assertEquals(status.getReasonPhrase(), body.getError());
        assertEquals(message, body.getMessage());
        assertEquals(path, body.getPath());
    }

    private void dummy(String value) {
    }
}
