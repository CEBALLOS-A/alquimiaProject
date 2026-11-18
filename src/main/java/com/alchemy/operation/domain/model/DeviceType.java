package com.alchemy.operation.domain.model;

import lombok.Getter;

@Getter
public enum DeviceType {

    STAR_LABS(-500, -200),
    CCPD(100, -100),
    STAR_SATELLITE(500, 100);

    private final int x;

    private final int y;

    DeviceType(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
