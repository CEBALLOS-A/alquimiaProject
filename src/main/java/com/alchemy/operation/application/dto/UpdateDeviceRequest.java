package com.alchemy.operation.application.dto;

import com.alchemy.operation.domain.model.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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

    @NotNull
    private DeviceType deviceType;

    @NotNull
    @PositiveOrZero
    private Double distance;

    @NotEmpty
    private List<@NotBlank String> messageFragment;
}
