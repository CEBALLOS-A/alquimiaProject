package com.alchemy.operation.infrastructure.configuration;

import com.alchemy.operation.application.usecase.OperationUseCase;
import com.alchemy.operation.domain.port.input.OperationInputPort;
import com.alchemy.operation.domain.port.output.OperationOutputPort;
import com.alchemy.operation.infrastructure.adapter.output.memory.InMemoryOperationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public OperationOutputPort operationOutputPort() {
        return new InMemoryOperationRepository();
    }

    @Bean
    public OperationInputPort operationInputPort(OperationOutputPort outputPort) {
        return new OperationUseCase(outputPort);
    }
}
