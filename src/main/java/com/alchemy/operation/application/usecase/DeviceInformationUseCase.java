package com.alchemy.operation.application.usecase;

import com.alchemy.operation.domain.model.DeviceType;
import com.alchemy.operation.domain.model.LaboratoryLocation;
import com.alchemy.operation.domain.model.SurveillanceDevice;
import com.alchemy.operation.domain.port.input.ProcessAllInformationUseCase;
import com.alchemy.operation.domain.port.input.UpdateDeviceInformationUseCase;
import com.alchemy.operation.domain.service.OperationDomainService;

import java.util.List;
import java.util.Objects;

public class DeviceInformationUseCase implements UpdateDeviceInformationUseCase, ProcessAllInformationUseCase {

    private final OperationDomainService domainService;

    public DeviceInformationUseCase(OperationDomainService domainService) {
        this.domainService = Objects.requireNonNull(domainService, "domainService must not be null");
    }

    @Override
    public SurveillanceDevice execute(SurveillanceDevice device) {
        return domainService.replace(device);
    }

    @Override
    public SurveillanceDevice execute(DeviceType deviceType, Double distance, List<String> messageFragment) {
        return domainService.update(deviceType, distance, messageFragment);
    }

    @Override
    public LaboratoryLocation execute() {
        return domainService.processAll();
    }

    @Override
    public LaboratoryLocation execute(List<SurveillanceDevice> devices) {
        return domainService.process(devices);
    }

    @Override
    public String message() {
        return domainService.message();
    }

    @Override
    public String message(List<SurveillanceDevice> devices) {
        return domainService.message(devices);
    }
}
