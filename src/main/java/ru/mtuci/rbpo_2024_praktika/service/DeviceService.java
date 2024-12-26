package ru.mtuci.rbpo_2024_praktika.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.model.Device;
import ru.mtuci.rbpo_2024_praktika.model.DeviceInfoRequest;
import ru.mtuci.rbpo_2024_praktika.repository.DeviceRepository;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public Device registerOrUpdateDevice(String macAddress, String deviceName, ApplicationUser user, boolean isAdmin) {
        Device device = deviceRepository.findByMacAddress(macAddress).orElse(new Device());

        if (device.getUserId() != null && !device.getUserId().equals(user.getId())) {
            if (!isAdmin) {
                throw new IllegalArgumentException("Устройство с MAC-адресом " + macAddress + " уже привязано к другому пользователю.");
            }
            return device;
        }

        device.setName(deviceName != null && !deviceName.trim().isEmpty() ? deviceName : "User Device");
        device.setMacAddress(macAddress);
        device.setUserId(user.getId());

        return deviceRepository.save(device);
    }

    public Device findDeviceByInfo(DeviceInfoRequest deviceInfo, ApplicationUser user) {
        return deviceRepository.findByMacAddressAndUser(deviceInfo.getMacAddress(), user).orElse(null);
    }

    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id).orElse(null);
    }
}
