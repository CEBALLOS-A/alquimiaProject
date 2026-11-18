package com.alchemy.operation.domain.service;

import com.alchemy.operation.domain.exception.DomainException;
import com.alchemy.operation.domain.exception.LocationCalculationException;
import com.alchemy.operation.domain.model.DeviceType;
import com.alchemy.operation.domain.model.LaboratoryLocation;
import com.alchemy.operation.domain.model.SurveillanceDevice;
import com.alchemy.operation.domain.port.output.DeviceRepositoryPort;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OperationDomainService {

    private static final DeviceType[] REFERENCE_DEVICES = {
            DeviceType.STAR_LABS,
            DeviceType.CCPD,
            DeviceType.SATELLITE
    };
    private static final double EPSILON = 1e-9;
    private static final double DISTANCE_TOLERANCE = 1e-2;
    private static final String DISTANCE_REQUIRED_MESSAGE = "Distance is required for %s";
    private static final String DISTANCE_INVALID_MESSAGE = "Distance must be finite and non-negative for %s";

    private final DeviceRepositoryPort deviceRepository;

    public OperationDomainService(DeviceRepositoryPort deviceRepository) {
        this.deviceRepository = Objects.requireNonNull(deviceRepository, "deviceRepository must not be null");
    }

    public SurveillanceDevice register(SurveillanceDevice device) {
        Objects.requireNonNull(device, "device must not be null");
        if (deviceRepository.existsByDeviceType(device.getDeviceType())) {
            throw new DomainException("Device is already registered: " + device.getDeviceType());
        }
        return deviceRepository.save(device);
    }

    public SurveillanceDevice replace(SurveillanceDevice device) {
        Objects.requireNonNull(device, "device must not be null");
        return deviceRepository.replace(device);
    }

    public SurveillanceDevice update(DeviceType deviceType, Double distance, List<String> messageFragment) {
        Objects.requireNonNull(deviceType, "deviceType must not be null");
        if (distance == null && messageFragment == null) {
            throw new DomainException("At least one field must be provided for a partial update");
        }
        return deviceRepository.update(deviceType, distance, messageFragment);
    }

    public void delete(DeviceType deviceType) {
        Objects.requireNonNull(deviceType, "deviceType must not be null");
        deviceRepository.delete(deviceType);
    }

    public List<SurveillanceDevice> findAll() {
        return deviceRepository.findAll();
    }

    public LaboratoryLocation calculateLocation(Double starLabsDistance, Double ccpdDistance, Double satelliteDistance) {
        return trilaterate(REFERENCE_DEVICES[0], starLabsDistance,
                REFERENCE_DEVICES[1], ccpdDistance,
                REFERENCE_DEVICES[2], satelliteDistance);
    }

    public LaboratoryLocation processAll() {
        return locate(deviceRepository.findAll());
    }

    public LaboratoryLocation process(List<SurveillanceDevice> devices) {
        return locate(devices);
    }

    public String message() {
        return buildMessage(deviceRepository.findAll());
    }

    public String message(List<SurveillanceDevice> devices) {
        return buildMessage(devices);
    }

    private LaboratoryLocation locate(List<SurveillanceDevice> devices) {
        Map<DeviceType, SurveillanceDevice> byType = index(devices);
        Double starLabsDistance = byType.get(REFERENCE_DEVICES[0]).getDistance();
        Double ccpdDistance = byType.get(REFERENCE_DEVICES[1]).getDistance();
        Double satelliteDistance = byType.get(REFERENCE_DEVICES[2]).getDistance();
        requireDistance(REFERENCE_DEVICES[0], starLabsDistance);
        requireDistance(REFERENCE_DEVICES[1], ccpdDistance);
        requireDistance(REFERENCE_DEVICES[2], satelliteDistance);
        return calculateLocation(starLabsDistance, ccpdDistance, satelliteDistance);
    }

    private Map<DeviceType, SurveillanceDevice> index(List<SurveillanceDevice> devices) {
        if (devices == null || devices.size() != REFERENCE_DEVICES.length) {
            throw new DomainException("Complete information from " + REFERENCE_DEVICES.length + " devices is required");
        }
        Map<DeviceType, SurveillanceDevice> byType = new EnumMap<>(DeviceType.class);
        for (SurveillanceDevice device : devices) {
            Objects.requireNonNull(device, "device must not be null");
            if (byType.put(device.getDeviceType(), device) != null) {
                throw new DomainException("Duplicate device information: " + device.getDeviceType());
            }
        }
        for (DeviceType type : REFERENCE_DEVICES) {
            if (!byType.containsKey(type)) {
                throw new DomainException("Missing information for device: " + type);
            }
        }
        return byType;
    }

    private LaboratoryLocation trilaterate(DeviceType first, Double firstDistance,
                                           DeviceType second, Double secondDistance,
                                           DeviceType third, Double thirdDistance) {
        double d1 = requireDistance(first, firstDistance);
        double d2 = requireDistance(second, secondDistance);
        double d3 = requireDistance(third, thirdDistance);

        double x1 = first.getX();
        double y1 = first.getY();
        double x2 = second.getX();
        double y2 = second.getY();
        double x3 = third.getX();
        double y3 = third.getY();

        double a1 = 2 * (x1 - x2);
        double b1 = 2 * (y1 - y2);
        double c1 = (d2 * d2) - (d1 * d1) - ((x2 * x2) + (y2 * y2) - (x1 * x1) - (y1 * y1));
        double a2 = 2 * (x1 - x3);
        double b2 = 2 * (y1 - y3);
        double c2 = (d3 * d3) - (d1 * d1) - ((x3 * x3) + (y3 * y3) - (x1 * x1) - (y1 * y1));

        double determinant = (a1 * b2) - (a2 * b1);
        if (Math.abs(determinant) < EPSILON) {
            throw new LocationCalculationException("Device positions do not allow trilateration");
        }

        double x = ((c1 * b2) - (c2 * b1)) / determinant;
        double y = ((a1 * c2) - (a2 * c1)) / determinant;
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new LocationCalculationException("Distances do not produce a finite laboratory location");
        }

        double residual = Math.max(
                Math.abs(Math.hypot(x - x1, y - y1) - d1),
                Math.max(
                        Math.abs(Math.hypot(x - x2, y - y2) - d2),
                        Math.abs(Math.hypot(x - x3, y - y3) - d3)));
        if (residual > DISTANCE_TOLERANCE) {
            throw new LocationCalculationException("Distances do not define a common laboratory location");
        }
        return new LaboratoryLocation(x, y);
    }

    private double requireDistance(DeviceType deviceType, Double distance) {
        if (distance == null) {
            throw new LocationCalculationException(String.format(DISTANCE_REQUIRED_MESSAGE, deviceType));
        }
        if (!Double.isFinite(distance) || distance < 0) {
            throw new LocationCalculationException(String.format(DISTANCE_INVALID_MESSAGE, deviceType));
        }
        return distance;
    }

    private String buildMessage(List<SurveillanceDevice> devices) {
        if (devices == null || devices.isEmpty()) {
            throw new DomainException("No device information is available");
        }
        List<List<String>> fragmentsByDevice = new ArrayList<>();
        int positionCount = 0;
        for (SurveillanceDevice device : devices) {
            if (device == null) {
                throw new DomainException("Device information must not be null");
            }
            List<String> fragments = device.getMessageFragment();
            if (fragments == null || fragments.isEmpty()) {
                throw new DomainException("No message fragments are available for device: " + device.getDeviceType());
            }
            fragmentsByDevice.add(fragments);
            positionCount = Math.max(positionCount, fragments.size());
        }
        List<String> words = new ArrayList<>();
        for (int position = 0; position < positionCount; position++) {
            String selected = null;
            for (List<String> fragments : fragmentsByDevice) {
                if (position >= fragments.size()) {
                    continue;
                }
                String candidate = fragments.get(position);
                if (candidate == null || candidate.isBlank()) {
                    continue;
                }
                String word = candidate.trim();
                if (selected == null) {
                    selected = word;
                } else if (!selected.equals(word)) {
                    throw new DomainException("Conflicting message fragments at position " + position
                            + ": '" + selected + "' and '" + word + "'");
                }
            }
            if (selected == null) {
                throw new DomainException("Message cannot be reconstructed: no word at position " + position);
            }
            words.add(selected);
        }
        return String.join(" ", words);
    }
}
