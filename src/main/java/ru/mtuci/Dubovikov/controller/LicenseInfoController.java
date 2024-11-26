package ru.mtuci.Dubovikov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.Dubovikov.model.*;
import ru.mtuci.Dubovikov.service.DeviceService;
import ru.mtuci.Dubovikov.service.LicenseService;

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
        // Ищем устройство по информации
        Device device = deviceService.findDeviceByInfo(deviceInfo, authenticatedUser);

        if (device == null) {
            return ResponseEntity.status(404).body("Устройство не найдено");
        }

        // Получаем активные лицензии
        List<License> activeLicenses = licenseService.getActiveLicensesForDevice(device, authenticatedUser);

        if (activeLicenses.isEmpty()) {
            return ResponseEntity.status(404).body("Нет активных лицензий для этого устройства");
        }

        // Генерируем тикеты для всех активных лицензий
        List<Ticket> tickets = activeLicenses.stream()
                .map(license -> generateTicket(license, device))
                .collect(Collectors.toList());

        return ResponseEntity.ok(tickets);
    }

    private Ticket generateTicket(License license, Device device) {
        // Логика генерации тикета
        return new Ticket(
                30L, // Время жизни тикета в секундах
                license.getFirstActivationDate(),
                license.getEndingDate(),
                license.getOwner().getId(),
                device.getId(),
                license.getBlocked(),
                "Подпись" // Подпись тикета
        );
    }
}
