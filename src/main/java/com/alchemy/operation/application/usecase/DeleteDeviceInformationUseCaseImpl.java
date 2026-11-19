package com.alchemy.operation.application.usecase;

import com.alchemy.operation.domain.model.DeviceType;
import com.alchemy.operation.domain.port.input.DeleteDeviceInformationUseCase;
import com.alchemy.operation.domain.service.OperationDomainService;

import java.util.Objects;

public class DeleteDeviceInformationUseCaseImpl implements DeleteDeviceInformationUseCase {

    private final OperationDomainService domainService;

    public DeleteDeviceInformationUseCaseImpl(OperationDomainService domainService) {
        this.domainService = Objects.requireNonNull(domainService, "domainService must not be null");
    }

    @Override
    public void execute(DeviceType deviceType) {
        domainService.delete(deviceType);
    }
}
