package com.alchemy.operation.domain.port.input;

import com.alchemy.operation.domain.model.DeviceType;

public interface DeleteDeviceInformationUseCase {

    void execute(DeviceType deviceType);
}
