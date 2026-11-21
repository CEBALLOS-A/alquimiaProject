package com.alchemy.operation.infrastructure.adapter.input.rest;

import com.alchemy.operation.domain.model.DeviceType;
import com.alchemy.operation.domain.model.SurveillanceDevice;
import com.alchemy.operation.domain.port.input.GetStoredInformationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OperationControllerTest {

    private StubGetStoredInformationUseCase getStoredInformationUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        getStoredInformationUseCase = new StubGetStoredInformationUseCase();
        OperationController controller = new OperationController(
                device -> device,
                device -> device,
                device -> device,
                deviceType -> {
                },
                () -> null,
                getStoredInformationUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void returnsAllStoredDevices() throws Exception {
        getStoredInformationUseCase.devices = List.of(
                device(DeviceType.STAR_LABS, 10.0, "this", "", "message"),
                device(DeviceType.CCPD, 20.0, "", "is", ""));

        mockMvc.perform(get("/api/operations/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].deviceType").value("STAR_LABS"))
                .andExpect(jsonPath("$[0].distance").value(10.0))
                .andExpect(jsonPath("$[0].messageFragment[0]").value("this"))
                .andExpect(jsonPath("$[0].messageFragment[1]").value(""))
                .andExpect(jsonPath("$[1].deviceType").value("CCPD"))
                .andExpect(jsonPath("$[1].distance").value(20.0))
                .andExpect(jsonPath("$[1].messageFragment[0]").value(""));

        assertEquals(1, getStoredInformationUseCase.calls);
    }

    @Test
    void returnsEmptyListWhenNoDevicesAreStored() throws Exception {
        getStoredInformationUseCase.devices = List.of();

        mockMvc.perform(get("/api/operations/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        assertEquals(1, getStoredInformationUseCase.calls);
    }

    @Test
    void mapsInvalidDeviceTypeToBadRequest() throws Exception {
        mockMvc.perform(delete("/api/operations/devices/UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid device type"));
    }

    @Test
    void mapsInvalidRequestBodyValueToBadRequest() throws Exception {
        mockMvc.perform(post("/api/operations/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deviceType\":\"UNKNOWN\",\"distance\":0.0,\"messageFragment\":[\"estado\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid request value"));
    }

    private SurveillanceDevice device(DeviceType deviceType, double distance, String... fragments) {
        return SurveillanceDevice.builder()
                .deviceType(deviceType)
                .distance(distance)
                .messageFragment(Arrays.asList(fragments))
                .build();
    }

    private static class StubGetStoredInformationUseCase implements GetStoredInformationUseCase {

        private List<SurveillanceDevice> devices = List.of();
        private int calls;

        @Override
        public List<SurveillanceDevice> execute() {
            calls++;
            return devices;
        }
    }
}
