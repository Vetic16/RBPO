package ru.mtuci.Dubovikov.model;

import lombok.Data;

@Data
public class DeviceInfoRequest {
    private String macAddress;
    private String deviceName;
}
