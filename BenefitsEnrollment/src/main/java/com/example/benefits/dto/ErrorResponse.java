package com.example.benefits.dto;

import java.time.OffsetDateTime;

public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String errorCode,
        String message
) {
}
