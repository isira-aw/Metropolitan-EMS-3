package com.gms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GeneratorRequest {

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Name is required")
    private String name;

    private String capacity;

    private String locationName;

    private String ownerEmail;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String note;
}
