package com.alchemy.operation.domain.port.input;

import com.alchemy.operation.domain.model.LaboratoryLocation;
import com.alchemy.operation.domain.model.SurveillanceDevice;

import java.util.List;

public interface ProcessAllInformationUseCase {

    LaboratoryLocation execute();

    default LaboratoryLocation execute(List<SurveillanceDevice> devices) {
        throw new UnsupportedOperationException("Processing supplied information is not supported by this implementation");
    }

    default String message() {
        throw new UnsupportedOperationException("Message reconstruction is not supported by this implementation");
    }

    default String message(List<SurveillanceDevice> devices) {
        throw new UnsupportedOperationException("Message reconstruction is not supported by this implementation");
    }
}
