package ru.mtuci.Dubovikov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.Dubovikov.model.*;
import ru.mtuci.Dubovikov.service.LicenseService;
import ru.mtuci.Dubovikov.service.UserService;
import ru.mtuci.Dubovikov.service.DeviceService;

import java.util.List;

@RestController
@RequestMapping("/licenses")
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;
    private final UserService userService;
    private final DeviceService deviceService;

    @GetMapping("/{id}")
    public ResponseEntity<License> findById(@PathVariable Long id) {
        return licenseService.findLicenseByCode(id.toString())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    @GetMapping("/user")
    public ResponseEntity<List<License>> findAllByUser(@RequestParam Long userId) {
        ApplicationUser user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<License> licenses = licenseService.getLicensesByUser(user);
        return ResponseEntity.ok(licenses);
    }

    @PostMapping("/create")
    public ResponseEntity<License> createLicense(@RequestBody LicenseRequest licenseRequest) {
        // Получаем объект ApplicationUser по ownerId
        ApplicationUser owner = userService.getUserById(licenseRequest.getOwnerId());
        if (owner == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Создаем лицензию, передавая объект владельца
        License createdLicense = licenseService.createLicense(
                licenseRequest.getProductId(),
                owner, // Передаем объект ApplicationUser
                licenseRequest.getLicenseTypeId(),
                licenseRequest.getDescription(),
                licenseRequest.getDuration()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(createdLicense);
    }


    @PostMapping("/activate")
    public ResponseEntity<Ticket> activateLicense(@RequestParam String activationCode,
                                                  @RequestParam Long deviceId,
                                                  @RequestParam Long userId) {
        try {
            // Получаем Device и ApplicationUser из их сервисов
            Device device = deviceService.getDeviceById(deviceId);
            if (device == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            ApplicationUser user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Активируем лицензию
            Ticket ticket = licenseService.activateLicense(activationCode, device, user);
            return ResponseEntity.ok(ticket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @PostMapping("/block/{licenseId}")
    public ResponseEntity<Void> blockLicense(@PathVariable Long licenseId, @RequestParam Long adminId) {
        try {
            // Получаем объект ApplicationUser (администратора) из userService
            ApplicationUser admin = userService.getUserById(adminId);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Передаем объект admin в метод blockLicense
            licenseService.blockLicense(licenseId, admin);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}