package ru.mtuci.rbpo_2024_praktika.model;

import lombok.Data;

@Data
public class LicenseRequest {
    private Long productId;        // ID продукта
    private Long ownerId;          // ID владельца (если нужно)
    private Long licenseTypeId;    // ID типа лицензии
    private String code;           // Код лицензии
    private String description;     // Описание лицензии
    private Integer duration;       // Длительность лицензии
    private Integer maxDeviceCount; // Максимальное количество устройств
}