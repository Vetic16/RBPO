package ru.mtuci.Dubovikov.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.Dubovikov.model.DeviceLicense;
import ru.mtuci.Dubovikov.repository.DeviceLicenseRepository;
import ru.mtuci.Dubovikov.repository.DetailsRepository;
import ru.mtuci.Dubovikov.service.DeviceLicenseService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceLicenseServiceImpl implements DeviceLicenseService {

    private final DeviceLicenseRepository deviceLicenseRepository;
    private final DetailsRepository detailsRepository;

    @Override
    public void save(DeviceLicense deviceLicense) {
        deviceLicense.getDetails().forEach(details -> {
            details.setDeviceLicense(deviceLicense);
            detailsRepository.save(details);
        });
        deviceLicenseRepository.save(deviceLicense);
    }

    @Override
    public List<DeviceLicense> findAll() {
        return deviceLicenseRepository.findAll();
    }

    @Override
    public DeviceLicense findById(long id) {
        return deviceLicenseRepository.findById(id).orElse(new DeviceLicense());
    }
}
