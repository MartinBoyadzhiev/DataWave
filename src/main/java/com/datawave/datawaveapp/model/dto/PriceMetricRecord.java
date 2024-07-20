package com.datawave.datawaveapp.model.dto;

import java.time.Instant;

public record PriceMetricRecord(Instant timestamp,
                                float value,
                                String asset,
                                String exchange) {
}
