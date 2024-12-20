package ru.mtuci.Dubovikov.model;

import lombok.Data;

@Data
public class LicenseRequest {
    private Long productId;      // Идентификатор продукта
    private Long ownerId;        // Идентификатор владельца
    private Long licenseTypeId;  // Идентификатор типа лицензии
    private String description;  // Описание лицензии
    private Integer duration;    // Длительность действия лицензии (в днях)
}
