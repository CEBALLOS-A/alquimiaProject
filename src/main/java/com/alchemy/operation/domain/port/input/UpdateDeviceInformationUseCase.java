package com.alchemy.operation.domain.port.input;

import com.alchemy.operation.domain.model.DeviceType;
import com.alchemy.operation.domain.model.SurveillanceDevice;

import java.util.List;

public interface UpdateDeviceInformationUseCase {

    SurveillanceDevice execute(SurveillanceDevice device);

    default SurveillanceDevice execute(DeviceType deviceType, Double distance, List<String> messageFragment) {
        throw new UnsupportedOperationException("Partial update is not supported by this implementation");
    }
}
