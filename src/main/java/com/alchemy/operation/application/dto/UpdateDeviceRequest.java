package com.alchemy.operation.application.dto;

import com.alchemy.operation.domain.model.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeviceRequest {

    private DeviceType deviceType;

    @PositiveOrZero
    private Double distance;

    @Size(min = 1)
    private List<@NotBlank String> messageFragment;
}
