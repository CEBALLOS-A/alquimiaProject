package com.alchemy.operation.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class SurveillanceDevice {

    private DeviceType deviceType;

    private Double distance;

    private List<String> messageFragment;

    @Builder
    public SurveillanceDevice(DeviceType deviceType, Double distance, List<String> messageFragment) {
        setDeviceType(deviceType);
        setDistance(distance);
        setMessageFragment(messageFragment);
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = Objects.requireNonNull(deviceType, "deviceType must not be null");
    }

    public void setDistance(Double distance) {
        Objects.requireNonNull(distance, "distance must not be null");
        if (distance.isNaN() || distance.isInfinite() || distance < 0) {
            throw new IllegalArgumentException("distance must be a finite, non-negative value");
        }
        this.distance = distance;
    }

    public void setMessageFragment(List<String> messageFragment) {
        Objects.requireNonNull(messageFragment, "messageFragment must not be null");
        if (messageFragment.isEmpty()) {
            throw new IllegalArgumentException("messageFragment must not be empty");
        }
        if (messageFragment.stream().anyMatch(fragment -> fragment == null || fragment.isBlank())) {
            throw new IllegalArgumentException("messageFragment must not contain null or blank values");
        }
        this.messageFragment = new ArrayList<>(messageFragment);
    }
}
