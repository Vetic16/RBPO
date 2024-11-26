package ru.mtuci.Dubovikov.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.Dubovikov.model.DeviceLicense;
import ru.mtuci.Dubovikov.service.DeviceLicenseService;

import java.util.List;

@RestController
@RequestMapping("/device-license")
public class DeviceLicenseController {

    private final DeviceLicenseService deviceLicenseService;

    public DeviceLicenseController(DeviceLicenseService deviceLicenseService) {
        this.deviceLicenseService = deviceLicenseService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('read')")
    public List<DeviceLicense> findAll() {
        return deviceLicenseService.findAll();
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('modification')")
    public void save(@RequestBody DeviceLicense deviceLicense) {
        deviceLicenseService.save(deviceLicense);
    }
}
