package com.alchemy.operation.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class LaboratoryLocation {

    private Position position;

    @Builder
    public LaboratoryLocation(Position position) {
        setPosition(position);
    }

    public LaboratoryLocation(Double x, Double y) {
        this(Position.builder().x(x).y(y).build());
    }

    public void setPosition(Position position) {
        this.position = Objects.requireNonNull(position, "position must not be null");
    }
}
