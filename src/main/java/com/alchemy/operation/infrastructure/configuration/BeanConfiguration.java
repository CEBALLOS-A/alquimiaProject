package com.alchemy.operation.infrastructure.configuration;

import com.alchemy.operation.application.usecase.DeleteDeviceInformationUseCaseImpl;
import com.alchemy.operation.application.usecase.DeviceInformationUseCase;
import com.alchemy.operation.application.usecase.GetStoredInformationUseCaseImpl;
import com.alchemy.operation.application.usecase.RegisterDeviceInformationUseCaseImpl;
import com.alchemy.operation.application.usecase.ReplaceDeviceInformationUseCaseImpl;
import com.alchemy.operation.domain.port.input.DeleteDeviceInformationUseCase;
import com.alchemy.operation.domain.port.input.GetStoredInformationUseCase;
import com.alchemy.operation.domain.port.input.ProcessAllInformationUseCase;
import com.alchemy.operation.domain.port.input.RegisterDeviceInformationUseCase;
import com.alchemy.operation.domain.port.input.ReplaceDeviceInformationUseCase;
import com.alchemy.operation.domain.port.input.UpdateDeviceInformationUseCase;
import com.alchemy.operation.domain.port.output.DeviceRepositoryPort;
import com.alchemy.operation.domain.service.OperationDomainService;
import com.alchemy.operation.infrastructure.adapter.output.memory.InMemoryDeviceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

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
        return new RegisterDeviceInformationUseCaseImpl(domainService);
    }

    @Bean
    public ReplaceDeviceInformationUseCase replaceDeviceInformationUseCase(OperationDomainService domainService) {
        return new ReplaceDeviceInformationUseCaseImpl(domainService);
    }

    @Bean
    public DeleteDeviceInformationUseCase deleteDeviceInformationUseCase(OperationDomainService domainService) {
        return new DeleteDeviceInformationUseCaseImpl(domainService);
    }

    @Bean
    public GetStoredInformationUseCase getStoredInformationUseCase(OperationDomainService domainService) {
        return new GetStoredInformationUseCaseImpl(domainService);
    }

    @Bean
    public DeviceInformationUseCase deviceInformationUseCase(OperationDomainService domainService) {
        return new DeviceInformationUseCase(domainService);
    }
}
