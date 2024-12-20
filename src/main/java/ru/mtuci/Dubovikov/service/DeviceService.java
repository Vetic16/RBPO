package ru.mtuci.Dubovikov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.Dubovikov.model.ApplicationUser;
import ru.mtuci.Dubovikov.model.Device;
import ru.mtuci.Dubovikov.model.DeviceInfoRequest;
import ru.mtuci.Dubovikov.repository.DeviceRepository;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public Device registerOrUpdateDevice(String deviceInfo, ApplicationUser user) {
        // Находим устройство по идентификатору (например, MAC-адрес)
        Device device = deviceRepository.findByMacAddress(deviceInfo)
                .orElse(new Device());

        // Обновляем информацию об устройстве
        device.setName("User Device");
        device.setMacAddress(deviceInfo);
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
