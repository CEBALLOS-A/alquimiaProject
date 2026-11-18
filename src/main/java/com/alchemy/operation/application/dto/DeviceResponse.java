package com.alchemy.operation.application.dto;

import com.alchemy.operation.domain.model.DeviceType;
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
public class DeviceResponse {

    private DeviceType deviceType;

    private Double distance;

    private List<String> messageFragment;
}
