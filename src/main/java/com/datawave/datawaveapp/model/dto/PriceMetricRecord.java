package com.datawave.datawaveapp.model.dto;

import java.time.Instant;

public record PriceMetricRecord(Instant instant,
                                float value,
                                String asset,
                                String exchange) {
}
