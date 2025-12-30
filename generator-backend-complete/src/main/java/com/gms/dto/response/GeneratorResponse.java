package com.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class GeneratorResponse {
    private Long id;
    private String model;
    private String name;
    private String capacity;
    private String locationName;
    private String ownerEmail;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String note;
    private LocalDateTime createdAt;
}
