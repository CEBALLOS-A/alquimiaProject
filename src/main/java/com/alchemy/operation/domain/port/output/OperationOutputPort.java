package com.alchemy.operation.domain.port.output;

import com.alchemy.operation.domain.model.Operation;

import java.util.Optional;

public interface OperationOutputPort {

    Operation save(Operation operation);

    Optional<Operation> findById(String id);
}
