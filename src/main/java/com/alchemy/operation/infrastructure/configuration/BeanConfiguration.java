package com.alchemy.operation.infrastructure.configuration;

import com.alchemy.operation.application.usecase.DeviceInformationUseCase;
import com.alchemy.operation.application.usecase.OperationUseCase;
import com.alchemy.operation.domain.port.input.DeleteDeviceInformationUseCase;
import com.alchemy.operation.domain.port.input.GetStoredInformationUseCase;
import com.alchemy.operation.domain.port.input.OperationInputPort;
import com.alchemy.operation.domain.port.input.ProcessAllInformationUseCase;
import com.alchemy.operation.domain.port.input.RegisterDeviceInformationUseCase;
import com.alchemy.operation.domain.port.input.ReplaceDeviceInformationUseCase;
import com.alchemy.operation.domain.port.input.UpdateDeviceInformationUseCase;
import com.alchemy.operation.domain.port.output.DeviceRepositoryPort;
import com.alchemy.operation.domain.port.output.OperationOutputPort;
import com.alchemy.operation.domain.service.OperationDomainService;
import com.alchemy.operation.infrastructure.adapter.output.memory.InMemoryDeviceRepository;
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

    @Bean
    public DeviceRepositoryPort deviceRepositoryPort() {
        return new InMemoryDeviceRepository();
    }

    @Bean
    public OperationDomainService operationDomainService(DeviceRepositoryPort deviceRepositoryPort) {
        return new OperationDomainService(deviceRepositoryPort);
    }

    @Bean
    public RegisterDeviceInformationUseCase registerDeviceInformationUseCase(OperationDomainService domainService) {
        return domainService::register;
    }

    @Bean
    public ReplaceDeviceInformationUseCase replaceDeviceInformationUseCase(OperationDomainService domainService) {
        return domainService::replace;
    }

    @Bean
    public DeleteDeviceInformationUseCase deleteDeviceInformationUseCase(OperationDomainService domainService) {
        return domainService::delete;
    }

    @Bean
    public GetStoredInformationUseCase getStoredInformationUseCase(OperationDomainService domainService) {
        return domainService::findAll;
    }

    @Bean
    public DeviceInformationUseCase deviceInformationUseCase(OperationDomainService domainService) {
        return new DeviceInformationUseCase(domainService);
    }
}
