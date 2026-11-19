package com.alchemy.operation.application.usecase;

import com.alchemy.operation.domain.model.SurveillanceDevice;
import com.alchemy.operation.domain.port.input.RegisterDeviceInformationUseCase;
import com.alchemy.operation.domain.service.OperationDomainService;

import java.util.Objects;

public class RegisterDeviceInformationUseCaseImpl implements RegisterDeviceInformationUseCase {

    private final OperationDomainService domainService;

    public RegisterDeviceInformationUseCaseImpl(OperationDomainService domainService) {
        this.domainService = Objects.requireNonNull(domainService, "domainService must not be null");
    }

    @Override
    public SurveillanceDevice execute(SurveillanceDevice device) {
        return domainService.register(device);
    }
}
