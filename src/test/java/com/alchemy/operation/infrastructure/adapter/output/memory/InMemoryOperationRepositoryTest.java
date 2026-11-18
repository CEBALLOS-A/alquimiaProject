package com.alchemy.operation.infrastructure.adapter.output.memory;

import com.alchemy.operation.domain.model.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryOperationRepositoryTest {

    private InMemoryOperationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOperationRepository();
    }

    @Test
    void saveStoresOperationForLaterReads() {
        repository.save(Operation.builder().id("op-1").name("Alchemy").build());

        assertEquals("Alchemy", repository.findById("op-1").orElseThrow().getName());
    }

    @Test
    void findByIdReturnsEmptyWhenOperationDoesNotExist() {
        assertEquals(Optional.empty(), repository.findById("missing"));
    }

    @Test
    void saveOverwritesExistingOperation() {
        repository.save(Operation.builder().id("op-1").name("First").build());
        repository.save(Operation.builder().id("op-1").name("Second").build());

        assertEquals("Second", repository.findById("op-1").orElseThrow().getName());
        assertTrue(repository.findById("op-1").isPresent());
    }
}
