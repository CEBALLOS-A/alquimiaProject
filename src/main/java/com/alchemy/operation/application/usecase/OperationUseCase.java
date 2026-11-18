package com.alchemy.operation.application.usecase;

import com.alchemy.operation.domain.model.Operation;
import com.alchemy.operation.domain.port.input.OperationInputPort;
import com.alchemy.operation.domain.port.output.OperationOutputPort;

public class OperationUseCase implements OperationInputPort {

    private final OperationOutputPort outputPort;

    public OperationUseCase(OperationOutputPort outputPort) {
        this.outputPort = outputPort;
    }

    @Override
    public Operation create(Operation operation) {
        return null;
    }
}
