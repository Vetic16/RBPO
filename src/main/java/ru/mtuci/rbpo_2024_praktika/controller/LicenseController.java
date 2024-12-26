package ru.mtuci.rbpo_2024_praktika.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbpo_2024_praktika.model.*;
import ru.mtuci.rbpo_2024_praktika.service.ApplicationUserService;
import ru.mtuci.rbpo_2024_praktika.service.DeviceService;
import ru.mtuci.rbpo_2024_praktika.service.LicenseService;
import ru.mtuci.rbpo_2024_praktika.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

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
                    licenseRequest.getProductId(),
                    owner,
                    licenseRequest.getLicenseTypeId(),
                    licenseRequest.getDescription(),
                    licenseRequest.getDuration()
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

    @PostMapping("/block/{licenseId}")
    public ResponseEntity<?> blockLicense(@PathVariable Long licenseId, @RequestParam Long adminId) {
        try {
            ApplicationUser admin = userService.getUserById(adminId)
                    .orElseThrow(() -> new IllegalArgumentException("Администратор с ID " + adminId + " не найден"));


            licenseService.blockLicense(licenseId, admin);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при блокировке лицензии: " + e.getMessage());
        }
    }
    @GetMapping("/device/{deviceId}/active")
    public ResponseEntity<?> getActiveLicensesByDevice(@PathVariable Long deviceId,
                                                       @RequestParam Long userId) {
        try {
            // Проверка наличия устройства
            Device device = deviceService.getDeviceById(deviceId);
            if (device == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Устройство с ID " + deviceId + " не найдено");
            }

            ApplicationUser user = userService.getUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + userId + " не найден"));

            List<License> activeLicenses = licenseService.getActiveLicensesForDevice(device, user);

            if (activeLicenses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Активные лицензии для устройства с ID " + deviceId + " и пользователя с ID " + userId + " не найдены");
            }

            return ResponseEntity.ok(activeLicenses);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при получении активных лицензий: " + ex.getMessage());
        }
    }

    @PostMapping("/update-ending-date/{licenseId}")
    public ResponseEntity<?> updateLicenseEndingDate(@PathVariable Long licenseId,
                                                     @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Параметр 'userId' обязателен для выполнения операции");
        }

        try {
            License license = licenseService.findLicenseById(licenseId)
                    .orElseThrow(() -> new IllegalArgumentException("Лицензия с ID " + licenseId + " не найдена"));

            ApplicationUser user = userService.getUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + userId + " не найден"));

            if (!license.getOwnerId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Пользователь не является владельцем данной лицензии");
            }

            Ticket ticket = licenseService.updateOrRenewLicense(license.getCode(), user);
            return ResponseEntity.ok(ticket);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при обновлении лицензии: " + ex.getMessage());
        }
    }

    @GetMapping("/{licenseId}/license-ticket")
    public ResponseEntity<?> getLicenseTicketWithDevices(
            @PathVariable Long licenseId,
            @AuthenticationPrincipal User user,
            Authentication authentication) {
        try {
            License license = licenseService.findLicenseById(licenseId)
                    .orElseThrow(() -> new IllegalArgumentException("Лицензия с ID " + licenseId + " не найдена"));

            if (!hasAccessToLicense(license, user, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Вы не являетесь владельцем данной лицензии или не имеете прав для получения тикета.");
            }

            List<DeviceLicense> deviceLicenses = licenseService.getAllDeviceLicensesByLicenseId(licenseId);
            if (deviceLicenses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Связанных устройств для лицензии не найдено.");
            }

            String deviceIds = deviceLicenses.stream()
                    .map(deviceLicense -> String.valueOf(deviceLicense.getDevice().getId()))
                    .collect(Collectors.joining(","));

            Ticket ticket = licenseService.generateTicket(
                    license,
                    deviceIds,
                    "Тикет для лицензии с устройствами"
            );

            return ResponseEntity.ok(ticket);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при получении информации о лицензии: " + ex.getMessage());
        }
    }

    private boolean hasAccessToLicense(License license, User user, Authentication authentication) {
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        if (roles.contains("ADMIN")) {
            return true;
        }
        ApplicationUser appUser = applicationUserService.findByEmail(user.getUsername());
        return appUser != null && license.getOwnerId().equals(appUser.getId());
    }
}
