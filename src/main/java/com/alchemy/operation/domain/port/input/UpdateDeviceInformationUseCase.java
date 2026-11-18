package com.alchemy.operation.domain.port.input;

import com.alchemy.operation.domain.model.SurveillanceDevice;

public interface UpdateDeviceInformationUseCase {

    SurveillanceDevice execute(SurveillanceDevice device);
}
