package ru.mtuci.rbpo_2024_praktika.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.model.DeviceLicense;
import ru.mtuci.rbpo_2024_praktika.repository.DeviceLicenseRepository;
import ru.mtuci.rbpo_2024_praktika.service.DeviceLicenseService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceLicenseServiceImpl implements DeviceLicenseService {

    private final DeviceLicenseRepository deviceLicenseRepository;

    @Override
    public void save(DeviceLicense deviceLicense) {
        deviceLicenseRepository.save(deviceLicense);
    }

    @Override
    public List<DeviceLicense> findAll() {
        return deviceLicenseRepository.findAll();
    }

    @Override
    public DeviceLicense findById(long id) {
        return deviceLicenseRepository.findById(id).orElse(null);
    }

    @Override
    public boolean update(long id, DeviceLicense updatedDeviceLicense) {
        if (deviceLicenseRepository.existsById(id)) {
            DeviceLicense existingDeviceLicense = deviceLicenseRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("DeviceLicense not found with id: " + id));
            existingDeviceLicense.setLicense(updatedDeviceLicense.getLicense());
            existingDeviceLicense.setDevice(updatedDeviceLicense.getDevice());
            existingDeviceLicense.setActivationDate(updatedDeviceLicense.getActivationDate());
            existingDeviceLicense.setDetails(updatedDeviceLicense.getDetails());
            deviceLicenseRepository.save(existingDeviceLicense);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean delete(long id) {
        if (deviceLicenseRepository.existsById(id)) {
            deviceLicenseRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
