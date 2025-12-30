package com.gms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImageUploadRequest {

    @NotBlank(message = "Image data is required")
    private String imageData; // Base64 encoded image

    private String fileName;
    private String contentType;
}
