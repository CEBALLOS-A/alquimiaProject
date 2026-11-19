# Domain model

All types live in `com.alchemy.operation.domain.model`.

## Overview

```text
Operation (aggregate root)
  └── id, name

DeviceType (enum) ── referenced by ──> SurveillanceDevice
Position ── wrapped by ──> LaboratoryLocation

SurveillanceDevice
  ├── deviceType: DeviceType
  ├── distance: Double
  └── messageFragment: List<String>
```

## `SurveillanceDevice`

Represents one reference device with its measured distance and ordered message fragments.

| Field | Type | Invariant |
| --- | --- | --- |
| `deviceType` | `DeviceType` | Required. |
| `distance` | `Double` | Required, finite, non-negative. |
| `messageFragment` | `List<String>` | Required, non-empty; null and whitespace-only entries are rejected. |

The constructor (also exposed as a Lombok builder) applies the setters, so all invariants are enforced at construction time. `setMessageFragment` copies the input list, preventing later external mutation.

An empty string is accepted by the model, but `OperationDomainService.buildMessage` skips blank fragments during reconstruction.

## `DeviceType`

Enum of the three reference devices, with fixed coordinates:

| Constant | x | y |
| --- | --- | --- |
| `STAR_LABS` | -500 | -200 |
| `CCPD` | 100 | -100 |
| `STAR_SATELLITE` | 500 | 100 |

`SATELLITE` is a static alias for `STAR_SATELLITE`. Coordinates are read through Lombok-generated getters and are used by the trilateration logic.

## `Position`

A coordinate pair with `x` and `y` fields. Both are required (non-null); no additional range or finiteness validation is applied. Created through the builder or constructor, and wrapped by `LaboratoryLocation`.

## `LaboratoryLocation`

Wraps a single `Position`. It can be built from a `Position` or directly from `x`/`y` doubles. The position is required. This is the result returned by location processing in the domain service.

## `Operation`

Placeholder aggregate root implementing `AggregateRoot`, with `id` and `name`. Lombok provides getters, setters, builders, and no-arg/all-arg constructors. Business behavior has not been added yet.
