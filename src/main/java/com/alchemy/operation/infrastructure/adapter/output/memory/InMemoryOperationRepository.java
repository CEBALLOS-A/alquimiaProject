package com.alchemy.operation.infrastructure.adapter.output.memory;

import com.alchemy.operation.domain.model.Operation;
import com.alchemy.operation.domain.port.output.OperationOutputPort;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOperationRepository implements OperationOutputPort {

    private final Map<String, Operation> operations = new ConcurrentHashMap<>();

    @Override
    public Operation save(Operation operation) {
        return null;
    }

    @Override
    public Optional<Operation> findById(String id) {
        return Optional.empty();
    }
}
