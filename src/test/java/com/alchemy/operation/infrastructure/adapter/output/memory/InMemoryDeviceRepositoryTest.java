package com.alchemy.operation.infrastructure.adapter.output.memory;

import com.alchemy.operation.domain.exception.DomainException;
import com.alchemy.operation.domain.model.DeviceType;
import com.alchemy.operation.domain.model.SurveillanceDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryDeviceRepositoryTest {

    private InMemoryDeviceRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDeviceRepository();
    }

    @Test
    void saveStoresDeviceForLaterReads() {
        repository.save(device(DeviceType.CCPD, 10.0, "alpha", "beta"));

        assertTrue(repository.existsByDeviceType(DeviceType.CCPD));
        SurveillanceDevice found = repository.findByDeviceType(DeviceType.CCPD).orElseThrow();
        assertEquals(DeviceType.CCPD, found.getDeviceType());
        assertEquals(10.0, found.getDistance());
        assertEquals(List.of("alpha", "beta"), found.getMessageFragment());
    }

    @Test
    void findAllReturnsAllStoredDevices() {
        repository.save(device(DeviceType.CCPD, 10.0, "alpha"));
        repository.save(device(DeviceType.STAR_LABS, 20.0, "beta"));

        List<SurveillanceDevice> all = repository.findAll();

        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(device -> device.getDeviceType() == DeviceType.CCPD));
        assertTrue(all.stream().anyMatch(device -> device.getDeviceType() == DeviceType.STAR_LABS));
    }

    @Test
    void updateChangesOnlyDistanceWhenMessageFragmentIsNull() {
        repository.save(device(DeviceType.CCPD, 10.0, "alpha", "beta"));

        SurveillanceDevice updated = repository.update(DeviceType.CCPD, 25.0, null);

        assertEquals(25.0, updated.getDistance());
        assertEquals(List.of("alpha", "beta"), updated.getMessageFragment());
        assertEquals(25.0, repository.findByDeviceType(DeviceType.CCPD).orElseThrow().getDistance());
    }

    @Test
    void updateChangesOnlyMessageFragmentWhenDistanceIsNull() {
        repository.save(device(DeviceType.CCPD, 10.0, "alpha", "beta"));

        SurveillanceDevice updated = repository.update(DeviceType.CCPD, null, List.of("gamma"));

        assertEquals(10.0, updated.getDistance());
        assertEquals(List.of("gamma"), updated.getMessageFragment());
        assertEquals(List.of("gamma"), repository.findByDeviceType(DeviceType.CCPD).orElseThrow().getMessageFragment());
    }

    @Test
    void updateRejectsEmptyPatch() {
        assertThrows(DomainException.class, () -> repository.update(DeviceType.CCPD, null, null));
    }

    @Test
    void updateRejectsMissingDevice() {
        assertThrows(DomainException.class, () -> repository.update(DeviceType.CCPD, 10.0, null));
    }

    @Test
    void replaceOverwritesStoredInformation() {
        repository.save(device(DeviceType.CCPD, 10.0, "alpha"));

        SurveillanceDevice replacement = device(DeviceType.CCPD, 99.0, "omega");
        SurveillanceDevice result = repository.replace(replacement);

        assertEquals(99.0, result.getDistance());
        assertEquals(List.of("omega"), result.getMessageFragment());
        assertEquals(result, repository.findByDeviceType(DeviceType.CCPD).orElseThrow());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void replaceRejectsMissingDevice() {
        assertThrows(DomainException.class,
                () -> repository.replace(device(DeviceType.CCPD, 10.0, "alpha")));
        assertFalse(repository.existsByDeviceType(DeviceType.CCPD));
    }

    @Test
    void deleteRemovesDeviceAndIsIdempotent() {
        repository.save(device(DeviceType.CCPD, 10.0, "alpha"));

        repository.delete(DeviceType.CCPD);

        assertTrue(repository.findByDeviceType(DeviceType.CCPD).isEmpty());
        assertTrue(repository.findAll().isEmpty());
        assertDoesNotThrow(() -> repository.delete(DeviceType.CCPD));
    }

    @Test
    void findAllReturnsSnapshot() {
        repository.save(device(DeviceType.CCPD, 10.0, "alpha"));
        List<SurveillanceDevice> snapshot = repository.findAll();

        repository.delete(DeviceType.CCPD);

        assertEquals(1, snapshot.size());
    }

    @Test
    void storedDataIsIsolatedFromCallerMutations() {
        SurveillanceDevice input = device(DeviceType.CCPD, 10.0, "alpha", "beta");
        repository.save(input);
        input.getMessageFragment().add("mutated");

        assertEquals(List.of("alpha", "beta"), repository.findByDeviceType(DeviceType.CCPD).orElseThrow().getMessageFragment());

        SurveillanceDevice found = repository.findByDeviceType(DeviceType.CCPD).orElseThrow();
        found.getMessageFragment().add("another");
        assertEquals(List.of("alpha", "beta"), repository.findByDeviceType(DeviceType.CCPD).orElseThrow().getMessageFragment());
    }

    private SurveillanceDevice device(DeviceType deviceType, double distance, String... fragments) {
        return SurveillanceDevice.builder()
                .deviceType(deviceType)
                .distance(distance)
                .messageFragment(List.of(fragments))
                .build();
    }
}
