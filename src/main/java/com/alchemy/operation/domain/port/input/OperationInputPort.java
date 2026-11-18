package com.alchemy.operation.domain.port.input;

import com.alchemy.operation.domain.model.Operation;

public interface OperationInputPort {

    Operation create(Operation operation);
}
