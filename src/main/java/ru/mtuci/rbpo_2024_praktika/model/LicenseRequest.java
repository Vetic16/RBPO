package ru.mtuci.rbpo_2024_praktika.model;

import lombok.Data;

@Data
public class LicenseRequest {
    private Long productId;
    private Long ownerId;
    private Long licenseTypeId;
    private String description;
    private Integer duration;
}
