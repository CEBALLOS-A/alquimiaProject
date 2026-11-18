package com.alchemy.operation.domain.port.output;

import com.alchemy.operation.domain.model.DeviceType;
import com.alchemy.operation.domain.model.SurveillanceDevice;

import java.util.List;
import java.util.Optional;

public interface DeviceRepositoryPort {

    SurveillanceDevice save(SurveillanceDevice device);

    Optional<SurveillanceDevice> findByDeviceType(DeviceType deviceType);

    List<SurveillanceDevice> findAll();

    boolean existsByDeviceType(DeviceType deviceType);

    void deleteByDeviceType(DeviceType deviceType);
}
