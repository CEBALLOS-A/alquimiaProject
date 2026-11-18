package com.alchemy.operation.domain.service;

import com.alchemy.operation.domain.exception.DomainException;
import com.alchemy.operation.domain.exception.LocationCalculationException;
import com.alchemy.operation.domain.model.DeviceType;
import com.alchemy.operation.domain.model.LaboratoryLocation;
import com.alchemy.operation.domain.model.SurveillanceDevice;
import com.alchemy.operation.domain.model.Position;
import com.alchemy.operation.infrastructure.adapter.output.memory.InMemoryDeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperationDomainServiceTest {

    private InMemoryDeviceRepository repository;
    private OperationDomainService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDeviceRepository();
        service = new OperationDomainService(repository);
    }

    @Test
    void calculatesLocationAtOriginFromThreeDistances() {
        LaboratoryLocation location = service.calculateLocation(
                distance(DeviceType.STAR_LABS, 0.0, 0.0),
                distance(DeviceType.CCPD, 0.0, 0.0),
                distance(DeviceType.SATELLITE, 0.0, 0.0));

        assertPosition(location, 0.0, 0.0);
    }

    @Test
    void calculatesNonTrivialLocationFromThreeDistances() {
        LaboratoryLocation location = service.calculateLocation(
                distance(DeviceType.STAR_LABS, 100.0, 50.0),
                distance(DeviceType.CCPD, 100.0, 50.0),
                distance(DeviceType.SATELLITE, 100.0, 50.0));

        assertPosition(location, 100.0, 50.0);
    }

    @Test
    void rejectsNullDistance() {
        LocationCalculationException exception = assertThrows(LocationCalculationException.class,
                () -> service.calculateLocation(null, 150.0, 403.0));

        assertEquals("Distance is required for STAR_LABS", exception.getMessage());
        assertTrue(exception instanceof DomainException);
    }

    @Test
    void rejectsNullDistanceInProcessBeforeCalculation() {
        LocationCalculationException exception = assertThrows(LocationCalculationException.class,
                () -> service.process(List.of(
                        device(DeviceType.STAR_LABS, 10.0),
                        deviceWithNullableDistance(DeviceType.CCPD, null),
                        device(DeviceType.SATELLITE, 10.0))));

        assertEquals("Distance is required for CCPD", exception.getMessage());
        assertTrue(exception instanceof DomainException);
    }

    @Test
    void usesSameValidationMessageForProcessAndCalculateLocation() {
        String calculateMessage = assertThrows(LocationCalculationException.class,
                () -> service.calculateLocation(null, 150.0, 403.0)).getMessage();
        String processMessage = assertThrows(LocationCalculationException.class,
                () -> service.process(List.of(
                        deviceWithNullableDistance(DeviceType.STAR_LABS, null),
                        device(DeviceType.CCPD, 150.0),
                        device(DeviceType.SATELLITE, 403.0)))).getMessage();

        assertEquals(calculateMessage, processMessage);
    }

    @Test
    void rejectsNegativeDistance() {
        assertThrows(LocationCalculationException.class,
                () -> service.calculateLocation(-1.0, 150.0, 403.0));
    }

    @Test
    void rejectsNonFiniteDistance() {
        assertThrows(LocationCalculationException.class,
                () -> service.calculateLocation(Double.NaN, 150.0, 403.0));
        assertThrows(LocationCalculationException.class,
                () -> service.calculateLocation(650.0, Double.POSITIVE_INFINITY, 403.0));
    }

    @Test
    void rejectsDistancesWithoutCommonIntersection() {
        assertThrows(LocationCalculationException.class,
                () -> service.calculateLocation(10.0, 10.0, 10.0));
    }

    @Test
    void integratesTrilaterationWithProcessAll() {
        repository.save(device(DeviceType.STAR_LABS, distance(DeviceType.STAR_LABS, 100.0, 50.0)));
        repository.save(device(DeviceType.CCPD, distance(DeviceType.CCPD, 100.0, 50.0)));
        repository.save(device(DeviceType.SATELLITE, distance(DeviceType.SATELLITE, 100.0, 50.0)));

        assertPosition(service.processAll(), 100.0, 50.0);
    }

    @Test
    void reconstructsMessageFromAlignedFragments() {
        String message = service.message(List.of(
                device(DeviceType.STAR_LABS, "", "es", "", "mensaje"),
                device(DeviceType.CCPD, "este", "", "un", ""),
                device(DeviceType.SATELLITE, "este", "es", "un", "mensaje")));

        assertEquals("este es un mensaje", message);
    }

    @Test
    void selectsFirstNonEmptyWordForEachPosition() {
        String message = service.message(List.of(
                device(DeviceType.STAR_LABS, "this", "", "message"),
                device(DeviceType.CCPD, "this", "is", "message")));

        assertEquals("this is message", message);
    }

    @Test
    void reconstructsMessageFromSingleDevice() {
        assertEquals("this is a message",
                service.message(List.of(device(DeviceType.STAR_LABS, "this", "is", "a", "message"))));
    }

    @Test
    void rejectsConflictingFragmentsAtSameIndex() {
        DomainException exception = assertThrows(DomainException.class, () -> service.message(List.of(
                device(DeviceType.STAR_LABS, "this", "is", "a", "message"),
                device(DeviceType.CCPD, "that", "is", "a", "message"))));

        assertTrue(exception.getMessage().contains("position 0"));
        assertTrue(exception.getMessage().contains("this"));
        assertTrue(exception.getMessage().contains("that"));
    }

    @Test
    void detectsConflictAtLaterPositionWithDifferentFragmentLengths() {
        DomainException exception = assertThrows(DomainException.class, () -> service.message(List.of(
                device(DeviceType.STAR_LABS, "this", "is", "wrong", "message"),
                device(DeviceType.CCPD, "this", "is", "a"))));

        assertTrue(exception.getMessage().contains("position 2"));
    }

    @Test
    void rejectsImpossibleReconstructionWhenNoWordAtPosition() {
        DomainException exception = assertThrows(DomainException.class, () -> service.message(List.of(
                device(DeviceType.STAR_LABS, "este", "es", "", "mensaje"),
                device(DeviceType.CCPD, "", "", "", ""))));

        assertTrue(exception.getMessage().contains("position 2"));
    }

    @Test
    void handlesDevicesWithDifferentFragmentLengths() {
        String message = service.message(List.of(
                device(DeviceType.STAR_LABS, "", "is"),
                device(DeviceType.CCPD, "this"),
                device(DeviceType.SATELLITE, "", "", "a", "message")));

        assertEquals("this is a message", message);
    }

    @Test
    void preservesNumericPositionOrderWhenFragmentsAreDiscoveredOutOfOrder() {
        String message = service.message(List.of(
                device(DeviceType.STAR_LABS, "", "", "", "message"),
                device(DeviceType.CCPD, "this"),
                device(DeviceType.SATELLITE, "", "is", "a")));

        assertEquals("this is a message", message);
    }

    @Test
    void trimsWordsBeforeReconstruction() {
        String message = service.message(List.of(
                device(DeviceType.STAR_LABS, " this ", "is", "a ", " message ")));

        assertEquals("this is a message", message);
    }

    @Test
    void reconstructsMessageFromRepositoryBackedDevices() {
        repository.save(device(DeviceType.STAR_LABS, "", "is", "", "message"));
        repository.save(device(DeviceType.CCPD, "this", "", "a", ""));
        repository.save(device(DeviceType.SATELLITE, "this", "is", "a", "message"));

        assertEquals("this is a message", service.message());
    }

    @Test
    void rejectsWhenNoDevicesAreProvided() {
        assertThrows(DomainException.class, () -> service.message(List.of()));
        assertThrows(DomainException.class, () -> service.message(null));
    }

    @Test
    void rejectsNullDevice() {
        DomainException exception = assertThrows(DomainException.class,
                () -> service.message(Arrays.asList(device(DeviceType.STAR_LABS, "this"), null)));

        assertEquals("Device information must not be null", exception.getMessage());
    }

    @Test
    void rejectsDeviceWithoutMessageFragments() {
        DomainException exception = assertThrows(DomainException.class,
                () -> service.message(List.of(deviceWithoutFragments(DeviceType.STAR_LABS))));

        assertTrue(exception.getMessage().contains("STAR_LABS"));
    }

    @Test
    void rejectsWhenAllFragmentsAreEmpty() {
        assertThrows(DomainException.class, () -> service.message(List.of(
                device(DeviceType.STAR_LABS, "", ""),
                device(DeviceType.CCPD, "", ""))));
    }

    @Test
    void rejectsEmptyFragmentList() {
        DomainException exception = assertThrows(DomainException.class,
                () -> service.message(List.of(deviceWithEmptyFragments(DeviceType.STAR_LABS))));

        assertTrue(exception.getMessage().contains("STAR_LABS"));
    }

    private double distance(DeviceType deviceType, double x, double y) {
        return Math.hypot(x - deviceType.getX(), y - deviceType.getY());
    }

    private SurveillanceDevice device(DeviceType deviceType, double distance) {
        return SurveillanceDevice.builder()
                .deviceType(deviceType)
                .distance(distance)
                .messageFragment(List.of("fragment"))
                .build();
    }

    private SurveillanceDevice device(DeviceType deviceType, String... fragments) {
        return SurveillanceDevice.builder()
                .deviceType(deviceType)
                .distance(0.0)
                .messageFragment(Arrays.asList(fragments))
                .build();
    }

    private SurveillanceDevice deviceWithNullableDistance(DeviceType deviceType, Double distance) {
        SurveillanceDevice device = new SurveillanceDevice();
        device.setDeviceType(deviceType);
        if (distance != null) {
            device.setDistance(distance);
        }
        device.setMessageFragment(List.of("fragment"));
        return device;
    }

    private SurveillanceDevice deviceWithoutFragments(DeviceType deviceType) {
        SurveillanceDevice device = new SurveillanceDevice();
        device.setDeviceType(deviceType);
        device.setDistance(0.0);
        return device;
    }

    private SurveillanceDevice deviceWithEmptyFragments(DeviceType deviceType) {
        SurveillanceDevice device = new SurveillanceDevice();
        device.setDeviceType(deviceType);
        device.setDistance(0.0);
        try {
            java.lang.reflect.Field field = SurveillanceDevice.class.getDeclaredField("messageFragment");
            field.setAccessible(true);
            field.set(device, List.of());
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
        return device;
    }

    private void assertPosition(LaboratoryLocation location, double x, double y) {
        Position position = location.getPosition();
        assertEquals(x, position.getX(), 1e-6);
        assertEquals(y, position.getY(), 1e-6);
    }
}
