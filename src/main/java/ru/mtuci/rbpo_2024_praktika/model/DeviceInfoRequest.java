package ru.mtuci.rbpo_2024_praktika.model;

public class DeviceInfoRequest {

    private String macAddress;
    private String deviceName;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
