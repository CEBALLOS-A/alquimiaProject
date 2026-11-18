package com.alchemy.operation.infrastructure.adapter.input.rest;

import com.alchemy.operation.domain.port.input.OperationInputPort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operations")
public class OperationController {

    private final OperationInputPort inputPort;

    public OperationController(OperationInputPort inputPort) {
        this.inputPort = inputPort;
    }
}
