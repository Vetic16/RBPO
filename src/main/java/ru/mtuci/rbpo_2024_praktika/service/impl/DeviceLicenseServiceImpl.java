package ru.mtuci.rbpo_2024_praktika.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.model.DeviceLicense;
import ru.mtuci.rbpo_2024_praktika.model.Details;
import ru.mtuci.rbpo_2024_praktika.repository.DeviceLicenseRepository;
import ru.mtuci.rbpo_2024_praktika.repository.DetailsRepository;
import ru.mtuci.rbpo_2024_praktika.service.DeviceLicenseService;

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
