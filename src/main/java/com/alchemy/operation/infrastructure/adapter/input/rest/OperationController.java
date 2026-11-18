package com.alchemy.operation.infrastructure.adapter.input.rest;

import com.alchemy.operation.application.dto.DeviceResponse;
import com.alchemy.operation.application.dto.LocationResponse;
import com.alchemy.operation.application.dto.MessageResponse;
import com.alchemy.operation.application.dto.ProcessResponse;
import com.alchemy.operation.application.dto.RegisterDeviceRequest;
import com.alchemy.operation.application.dto.ReplaceDeviceRequest;
import com.alchemy.operation.application.dto.UpdateDeviceRequest;
import com.alchemy.operation.domain.model.DeviceType;
import com.alchemy.operation.domain.model.LaboratoryLocation;
import com.alchemy.operation.domain.model.SurveillanceDevice;
import com.alchemy.operation.domain.port.input.DeleteDeviceInformationUseCase;
import com.alchemy.operation.domain.port.input.ProcessAllInformationUseCase;
import com.alchemy.operation.domain.port.input.RegisterDeviceInformationUseCase;
import com.alchemy.operation.domain.port.input.ReplaceDeviceInformationUseCase;
import com.alchemy.operation.domain.port.input.UpdateDeviceInformationUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
public class OperationController {

    private final RegisterDeviceInformationUseCase registerDeviceInformationUseCase;
    private final ReplaceDeviceInformationUseCase replaceDeviceInformationUseCase;
    private final UpdateDeviceInformationUseCase updateDeviceInformationUseCase;
    private final DeleteDeviceInformationUseCase deleteDeviceInformationUseCase;
    private final ProcessAllInformationUseCase processAllInformationUseCase;

    public OperationController(RegisterDeviceInformationUseCase registerDeviceInformationUseCase,
                               ReplaceDeviceInformationUseCase replaceDeviceInformationUseCase,
                               UpdateDeviceInformationUseCase updateDeviceInformationUseCase,
                               DeleteDeviceInformationUseCase deleteDeviceInformationUseCase,
                               ProcessAllInformationUseCase processAllInformationUseCase) {
        this.registerDeviceInformationUseCase = registerDeviceInformationUseCase;
        this.replaceDeviceInformationUseCase = replaceDeviceInformationUseCase;
        this.updateDeviceInformationUseCase = updateDeviceInformationUseCase;
        this.deleteDeviceInformationUseCase = deleteDeviceInformationUseCase;
        this.processAllInformationUseCase = processAllInformationUseCase;
    }

    @PostMapping("/process")
    public ProcessResponse processCompleteInformation(@Valid @RequestBody List<RegisterDeviceRequest> requests) {
        List<SurveillanceDevice> devices = requests.stream().map(this::toDomain).toList();
        return toResponse(processAllInformationUseCase.execute(devices), processAllInformationUseCase.message(devices));
    }

    @GetMapping("/process")
    public ProcessResponse processAccumulatedInformation() {
        return toResponse(processAllInformationUseCase.execute(), processAllInformationUseCase.message());
    }

    @PostMapping("/devices")
    public DeviceResponse registerDevice(@Valid @RequestBody RegisterDeviceRequest request) {
        return toResponse(registerDeviceInformationUseCase.execute(toDomain(request)));
    }

    @PutMapping("/devices")
    public DeviceResponse replaceDevice(@Valid @RequestBody ReplaceDeviceRequest request) {
        return toResponse(replaceDeviceInformationUseCase.execute(toDomain(request)));
    }

    @PatchMapping("/devices/{deviceType}")
    public DeviceResponse updateDevice(@PathVariable DeviceType deviceType,
                                       @Valid @RequestBody UpdateDeviceRequest request) {
        return toResponse(updateDeviceInformationUseCase.execute(deviceType, request.getDistance(), request.getMessageFragment()));
    }

    @DeleteMapping("/devices/{deviceType}")
    public MessageResponse deleteDevice(@PathVariable DeviceType deviceType) {
        deleteDeviceInformationUseCase.execute(deviceType);
        return MessageResponse.builder().message("Device deleted: " + deviceType).build();
    }

    private SurveillanceDevice toDomain(RegisterDeviceRequest request) {
        return SurveillanceDevice.builder()
                .deviceType(request.getDeviceType())
                .distance(request.getDistance())
                .messageFragment(request.getMessageFragment())
                .build();
    }

    private SurveillanceDevice toDomain(ReplaceDeviceRequest request) {
        return SurveillanceDevice.builder()
                .deviceType(request.getDeviceType())
                .distance(request.getDistance())
                .messageFragment(request.getMessageFragment())
                .build();
    }

    private DeviceResponse toResponse(SurveillanceDevice device) {
        return DeviceResponse.builder()
                .deviceType(device.getDeviceType())
                .distance(device.getDistance())
                .messageFragment(device.getMessageFragment())
                .build();
    }

    private ProcessResponse toResponse(LaboratoryLocation location, String message) {
        return ProcessResponse.builder()
                .location(LocationResponse.builder()
                        .x(location.getPosition().getX())
                        .y(location.getPosition().getY())
                        .build())
                .message(MessageResponse.builder().message(message).build())
                .build();
    }
}
