package com.alchemy.operation.domain.port.input;

import com.alchemy.operation.domain.model.SurveillanceDevice;

import java.util.List;

public interface GetStoredInformationUseCase {

    List<SurveillanceDevice> execute();
}
