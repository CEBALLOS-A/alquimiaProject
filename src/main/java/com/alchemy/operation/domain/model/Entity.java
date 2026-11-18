package com.alchemy.operation.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class Entity<ID> {

    private ID id;
}
