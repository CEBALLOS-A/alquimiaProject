package com.alchemy.operation.infrastructure.adapter.output.memory;

import com.alchemy.operation.domain.exception.DomainException;
import com.alchemy.operation.domain.model.DeviceType;
import com.alchemy.operation.domain.model.SurveillanceDevice;
import com.alchemy.operation.domain.port.output.DeviceRepositoryPort;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDeviceRepository implements DeviceRepositoryPort {

    private final ConcurrentHashMap<DeviceType, SurveillanceDevice> devices = new ConcurrentHashMap<>();

    @Override
    public SurveillanceDevice save(SurveillanceDevice device) {
        Objects.requireNonNull(device, "device must not be null");
        SurveillanceDevice stored = copy(device);
        devices.put(stored.getDeviceType(), stored);
        return copy(stored);
    }

    @Override
    public SurveillanceDevice update(DeviceType deviceType, Double distance, List<String> messageFragment) {
        Objects.requireNonNull(deviceType, "deviceType must not be null");
        if (distance == null && messageFragment == null) {
            throw new DomainException("At least one field must be provided for a partial update");
        }
        SurveillanceDevice updated = devices.computeIfPresent(deviceType, (type, current) -> copy(SurveillanceDevice.builder()
                .deviceType(type)
                .distance(distance == null ? current.getDistance() : distance)
                .messageFragment(messageFragment == null
                        ? List.copyOf(current.getMessageFragment())
                        : List.copyOf(messageFragment))
                .build()));
        if (updated == null) {
            throw new DomainException("Device is not registered: " + deviceType);
        }
        return copy(updated);
    }

    @Override
    public SurveillanceDevice replace(SurveillanceDevice device) {
        Objects.requireNonNull(device, "device must not be null");
        DeviceType deviceType = device.getDeviceType();
        SurveillanceDevice stored = copy(device);
        SurveillanceDevice previous = devices.replace(deviceType, stored);
        if (previous == null) {
            throw new DomainException("Device is not registered: " + deviceType);
        }
        return copy(stored);
    }

    @Override
    public void delete(DeviceType deviceType) {
        Objects.requireNonNull(deviceType, "deviceType must not be null");
        devices.remove(deviceType);
    }

    @Override
    public Optional<SurveillanceDevice> findByDeviceType(DeviceType deviceType) {
        return Optional.ofNullable(devices.get(deviceType)).map(this::copy);
    }

    @Override
    public List<SurveillanceDevice> findAll() {
        return devices.values().stream().map(this::copy).toList();
    }

    @Override
    public boolean existsByDeviceType(DeviceType deviceType) {
        return devices.containsKey(deviceType);
    }

    private SurveillanceDevice copy(SurveillanceDevice device) {
        return SurveillanceDevice.builder()
                .deviceType(device.getDeviceType())
                .distance(device.getDistance())
                .messageFragment(List.copyOf(device.getMessageFragment()))
                .build();
    }
}
