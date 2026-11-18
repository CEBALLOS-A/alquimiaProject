package com.alchemy.operation.domain.port.input;

import com.alchemy.operation.domain.model.SurveillanceDevice;

public interface ReplaceDeviceInformationUseCase {

    SurveillanceDevice execute(SurveillanceDevice device);
}
