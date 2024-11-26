package ru.mtuci.Dubovikov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.Dubovikov.model.DeviceLicense;
import ru.mtuci.Dubovikov.model.Details;
import ru.mtuci.Dubovikov.repository.DetailsRepository;
import ru.mtuci.Dubovikov.service.DeviceLicenseService;

@RestController
@RequestMapping("/details")
@RequiredArgsConstructor
public class DetailsController {

    private final DetailsRepository detailsRepository;
    private final DeviceLicenseService deviceLicenseService;

    @PostMapping("/{device_license_id}/save")
    public void save(@PathVariable(value = "device_license_id") Long deviceLicenseId,
                     @RequestBody Details details) {
        DeviceLicense deviceLicense = deviceLicenseService.findById(deviceLicenseId);
        details.setDeviceLicense(deviceLicense);
        detailsRepository.save(details);
    }
}
