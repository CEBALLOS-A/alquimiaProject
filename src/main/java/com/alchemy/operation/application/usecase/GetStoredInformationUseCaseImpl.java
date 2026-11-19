package com.alchemy.operation.application.usecase;

import com.alchemy.operation.domain.model.SurveillanceDevice;
import com.alchemy.operation.domain.port.input.GetStoredInformationUseCase;
import com.alchemy.operation.domain.service.OperationDomainService;

import java.util.List;
import java.util.Objects;

public class GetStoredInformationUseCaseImpl implements GetStoredInformationUseCase {

    private final OperationDomainService domainService;

    public GetStoredInformationUseCaseImpl(OperationDomainService domainService) {
        this.domainService = Objects.requireNonNull(domainService, "domainService must not be null");
    }

    @Override
    public List<SurveillanceDevice> execute() {
        return domainService.findAll();
    }
}
