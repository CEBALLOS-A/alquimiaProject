package com.alchemy.operation.infrastructure.adapter.output.memory;

import com.alchemy.operation.domain.model.Operation;
import com.alchemy.operation.domain.port.output.OperationOutputPort;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOperationRepository implements OperationOutputPort {

    private final ConcurrentHashMap<String, Operation> operations = new ConcurrentHashMap<>();

    @Override
    public Operation save(Operation operation) {
        Objects.requireNonNull(operation, "operation must not be null");
        String id = Objects.requireNonNull(operation.getId(), "operation id must not be null");
        Operation stored = Operation.builder()
                .id(id)
                .name(operation.getName())
                .build();
        operations.put(id, stored);
        return copy(stored);
    }

    @Override
    public Optional<Operation> findById(String id) {
        return Optional.ofNullable(operations.get(id)).map(this::copy);
    }

    private Operation copy(Operation operation) {
        return Operation.builder()
                .id(operation.getId())
                .name(operation.getName())
                .build();
    }
}
