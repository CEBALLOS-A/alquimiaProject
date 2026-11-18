package com.alchemy.operation.domain.port.output;

import com.alchemy.operation.domain.model.DeviceType;
import com.alchemy.operation.domain.model.SurveillanceDevice;

import java.util.List;
import java.util.Optional;

public interface DeviceRepositoryPort {

    SurveillanceDevice save(SurveillanceDevice device);

    SurveillanceDevice update(DeviceType deviceType, Double distance, List<String> messageFragment);

    SurveillanceDevice replace(SurveillanceDevice device);

    void delete(DeviceType deviceType);

    Optional<SurveillanceDevice> findByDeviceType(DeviceType deviceType);

    List<SurveillanceDevice> findAll();

    boolean existsByDeviceType(DeviceType deviceType);

    default void deleteByDeviceType(DeviceType deviceType) {
        delete(deviceType);
    }
}
