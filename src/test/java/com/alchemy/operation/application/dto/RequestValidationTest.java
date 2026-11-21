package com.alchemy.operation.application.dto;

import com.alchemy.operation.domain.model.DeviceType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestValidationTest {

    private static final List<String> FRAGMENTS_WITH_EMPTY_VALUES = List.of("", "es", "", "mensaje");

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void acceptsEmptyFragmentsInRegisterRequest() {
        assertTrue(validate(registerRequest(FRAGMENTS_WITH_EMPTY_VALUES)).isEmpty());
    }

    @Test
    void acceptsEmptyFragmentsInReplaceRequest() {
        assertTrue(validate(replaceRequest(FRAGMENTS_WITH_EMPTY_VALUES)).isEmpty());
    }

    @Test
    void acceptsEmptyFragmentsInUpdateRequest() {
        assertTrue(validate(updateRequest(FRAGMENTS_WITH_EMPTY_VALUES)).isEmpty());
    }

    @Test
    void rejectsNullFragmentInRegisterRequest() {
        assertMessageFragmentViolation(registerRequest(Arrays.asList("", null, "mensaje")));
    }

    @Test
    void rejectsNullFragmentInReplaceRequest() {
        assertMessageFragmentViolation(replaceRequest(Arrays.asList("", null, "mensaje")));
    }

    @Test
    void rejectsNullFragmentInUpdateRequest() {
        assertMessageFragmentViolation(updateRequest(Arrays.asList("", null, "mensaje")));
    }

    @Test
    void rejectsEmptyFragmentListInRegisterRequest() {
        assertMessageFragmentViolation(registerRequest(List.of()));
    }

    @Test
    void rejectsEmptyFragmentListInReplaceRequest() {
        assertMessageFragmentViolation(replaceRequest(List.of()));
    }

    @Test
    void rejectsEmptyFragmentListInUpdateRequest() {
        assertMessageFragmentViolation(updateRequest(List.of()));
    }

    @Test
    void rejectsNullFragmentListInRegisterRequest() {
        assertMessageFragmentViolation(registerRequest(null));
    }

    @Test
    void rejectsNullFragmentListInReplaceRequest() {
        assertMessageFragmentViolation(replaceRequest(null));
    }

    private RegisterDeviceRequest registerRequest(List<String> fragments) {
        return RegisterDeviceRequest.builder()
                .deviceType(DeviceType.STAR_LABS)
                .distance(0.0)
                .messageFragment(fragments)
                .build();
    }

    private ReplaceDeviceRequest replaceRequest(List<String> fragments) {
        return ReplaceDeviceRequest.builder()
                .deviceType(DeviceType.CCPD)
                .distance(0.0)
                .messageFragment(fragments)
                .build();
    }

    private UpdateDeviceRequest updateRequest(List<String> fragments) {
        return UpdateDeviceRequest.builder()
                .deviceType(DeviceType.STAR_LABS)
                .distance(0.0)
                .messageFragment(fragments)
                .build();
    }

    private void assertMessageFragmentViolation(Object request) {
        Set<ConstraintViolation<Object>> violations = validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().allMatch(violation ->
                violation.getPropertyPath().toString().startsWith("messageFragment")));
    }

    private <T> Set<ConstraintViolation<T>> validate(T request) {
        return validator.validate(request);
    }
}
