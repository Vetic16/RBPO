package ru.mtuci.rbpo_2024_praktika.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbpo_2024_praktika.model.*;
import ru.mtuci.rbpo_2024_praktika.service.DeviceService;
import ru.mtuci.rbpo_2024_praktika.service.LicenseService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/license-info")
@RequiredArgsConstructor
public class LicenseInfoController {

    private final DeviceService deviceService;
    private final LicenseService licenseService;

    @PostMapping
    public ResponseEntity<?> getLicenseInfo(@RequestBody DeviceInfoRequest deviceInfo,
                                            @AuthenticationPrincipal ApplicationUser authenticatedUser) {
        try {
            Device device = deviceService.findDeviceByInfo(deviceInfo, authenticatedUser);

            if (device == null) {
                return ResponseEntity.status(404).body("Устройство не найдено");
            }

            List<License> activeLicenses = licenseService.getActiveLicensesForDevice(device, authenticatedUser);

            if (activeLicenses.isEmpty()) {
                return ResponseEntity.status(404).body("Нет активных лицензий для этого устройства");
            }

            List<Ticket> tickets = activeLicenses.stream()
                    .map(license -> licenseService.generateTicket(license, device.getId().toString(), null))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(tickets);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(400).body("Некорректные данные: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Произошла ошибка: " + ex.getMessage());
        }
    }
}
