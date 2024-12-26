package ru.mtuci.rbpo_2024_praktika.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbpo_2024_praktika.model.DeviceLicense;
import ru.mtuci.rbpo_2024_praktika.service.DeviceLicenseService;

import java.util.List;

@RestController
@RequestMapping("/api/device-licenses")
@RequiredArgsConstructor
public class DeviceLicenseController {

    private final DeviceLicenseService deviceLicenseService;

    // Получить все записи DeviceLicense
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<List<DeviceLicense>> findAll() {
        List<DeviceLicense> deviceLicenses = deviceLicenseService.findAll();
        if (deviceLicenses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Если нет записей
        }
        return ResponseEntity.ok(deviceLicenses);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<DeviceLicense> findById(@PathVariable Long id) {
        DeviceLicense deviceLicense = deviceLicenseService.findById(id);
        if (deviceLicense == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Если запись не найдена
        }
        return ResponseEntity.ok(deviceLicense);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MODIFICATION')")
    public ResponseEntity<String> save(@RequestBody DeviceLicense deviceLicense) {
        try {
            deviceLicenseService.save(deviceLicense);
            return ResponseEntity.status(HttpStatus.CREATED).body("Device License successfully saved");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error saving Device License: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MODIFICATION')")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody DeviceLicense updatedDeviceLicense) {
        try {
            boolean updated = deviceLicenseService.update(id, updatedDeviceLicense);
            if (updated) {
                return ResponseEntity.ok("Device License successfully updated");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device License not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating Device License: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MODIFICATION')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            boolean deleted = deviceLicenseService.delete(id);
            if (deleted) {
                return ResponseEntity.ok("Device License successfully deleted");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device License not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error deleting Device License: " + e.getMessage());
        }
    }
}
