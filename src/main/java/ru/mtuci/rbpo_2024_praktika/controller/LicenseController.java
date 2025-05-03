package ru.mtuci.rbpo_2024_praktika.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbpo_2024_praktika.model.*;
import ru.mtuci.rbpo_2024_praktika.service.ApplicationUserService;
import ru.mtuci.rbpo_2024_praktika.service.DeviceService;
import ru.mtuci.rbpo_2024_praktika.service.LicenseService;
import ru.mtuci.rbpo_2024_praktika.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/licenses")
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;
    private final UserService userService;
    private final DeviceService deviceService;
    private final ApplicationUserService applicationUserService;

    @GetMapping("/{id}")
    public ResponseEntity<License> findById(@PathVariable Long id) {
        return licenseService.findLicenseByCode(id.toString())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/user")
    public ResponseEntity<?> findAllByUser(@RequestParam Long userId) {
        try {
            ApplicationUser user = userService.getUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + userId + " не найден"));

            List<License> licenses = licenseService.getLicensesByUser(user);

            // Проверка подписи каждой лицензии
            for (License license : licenses) {
                licenseService.checkSignatureOrMarkCorrupted(license, user);
            }

            return ResponseEntity.ok(licenses);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при получении лицензий: " + ex.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<License> createLicense(@RequestBody LicenseRequest licenseRequest, @AuthenticationPrincipal User user) {
        String email = user.getUsername();

        ApplicationUser owner = applicationUserService.findByEmail(email);
        if (owner == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            License createdLicense = licenseService.createLicense(
                    licenseRequest.getCode(),
                    licenseRequest.getLicenseTypeId(),
                    owner.getId(),
                    licenseRequest.getMaxDeviceCount(),
                    licenseRequest.getDuration(),
                    licenseRequest.getProductId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLicense);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(
            @RequestParam String activationCode,
            @RequestBody DeviceInfoRequest deviceInfo,
            @RequestParam(required = false) String targetUserEmail,
            @AuthenticationPrincipal User user) {
        if (activationCode == null || activationCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Activation code is required.");
        }

        if (deviceInfo == null ||
                deviceInfo.getMacAddress() == null || deviceInfo.getMacAddress().trim().isEmpty() ||
                deviceInfo.getDeviceName() == null || deviceInfo.getDeviceName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Device info (macAddress and deviceName) is required.");
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access is denied. Please log in.");
        }

        try {
            ApplicationUser authenticatedUser = applicationUserService.findByEmail(user.getUsername());
            boolean isAdmin = authenticatedUser.getRole() == ApplicationRole.ADMIN;

            ApplicationUser targetUser = authenticatedUser;
            if (targetUserEmail != null) {
                if (!isAdmin) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Only admins can activate licenses for other users.");
                }
                targetUser = applicationUserService.findByEmail(targetUserEmail);
                if (targetUser == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Target user not found for email: " + targetUserEmail);
                }
            }

            Device device = deviceService.registerOrUpdateDevice(
                    deviceInfo.getMacAddress(),
                    deviceInfo.getDeviceName(),
                    targetUser,
                    isAdmin
            );

            Ticket ticket = licenseService.activateLicense(activationCode, device, targetUser);
            return ResponseEntity.ok(ticket);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{licenseId}")
    public ResponseEntity<?> deleteLicense(@PathVariable Long licenseId, @AuthenticationPrincipal User user) {
        try {
            ApplicationUser admin = applicationUserService.findByEmail(user.getUsername());
            if (admin.getRole() != ApplicationRole.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can delete licenses.");
            }

            licenseService.deleteLicense(licenseId, admin);
            return ResponseEntity.ok("Лицензия успешно удалена.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при удалении лицензии: " + ex.getMessage());
        }
    }
}
