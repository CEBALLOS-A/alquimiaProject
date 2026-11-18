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
public class Position {

    private Double x;

    private Double y;

    @Builder
    public Position(Double x, Double y) {
        setX(x);
        setY(y);
    }

    public void setX(Double x) {
        this.x = Objects.requireNonNull(x, "x must not be null");
    }

    public void setY(Double y) {
        this.y = Objects.requireNonNull(y, "y must not be null");
    }
}
