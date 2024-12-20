package ru.mtuci.Dubovikov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mtuci.Dubovikov.model.ApplicationUser;
import ru.mtuci.Dubovikov.model.Device;
import ru.mtuci.Dubovikov.model.Ticket;
import ru.mtuci.Dubovikov.service.DeviceService;
import ru.mtuci.Dubovikov.service.LicenseService;

@RestController
@RequestMapping("/activation")
@RequiredArgsConstructor
public class ActivationController {

    private static final Logger logger = LoggerFactory.getLogger(ActivationController.class);

    private final LicenseService licenseService;
    private final DeviceService deviceService;

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(
            @RequestParam String activationCode,
            @RequestParam String deviceInfo) {

        // Проверяем параметры запроса
        if (activationCode == null || activationCode.trim().isEmpty()) {
            logger.warn("Activation code is missing or empty.");
            return ResponseEntity.badRequest().body("Activation code is required.");
        }

        if (deviceInfo == null || deviceInfo.trim().isEmpty()) {
            logger.warn("Device info is missing or empty.");
            return ResponseEntity.badRequest().body("Device info is required.");
        }

        // Получаем текущего пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("User is not authenticated.");
            return ResponseEntity.status(403).body("Access is denied. Please log in.");
        }

        Object principal = authentication.getPrincipal();

        ApplicationUser authenticatedUser;
        try {
            if (principal instanceof ApplicationUser) {
                authenticatedUser = (ApplicationUser) principal;
            } else {
                logger.error("Principal is not an instance of ApplicationUser: {}", principal.getClass());
                return ResponseEntity.status(500).body("Unexpected error. Please contact support.");
            }
        } catch (ClassCastException ex) {
            logger.error("Failed to cast principal to ApplicationUser: {}", ex.getMessage());
            return ResponseEntity.status(500).body("Unexpected error. Please contact support.");
        }

        try {
            // Регистрируем или обновляем устройство
            Device device = deviceService.registerOrUpdateDevice(deviceInfo, authenticatedUser);

            // Активация лицензии
            Ticket ticket = licenseService.activateLicense(activationCode, device, authenticatedUser);

            // Возвращаем успешный ответ
            logger.info("License activated successfully for user: {}", authenticatedUser.getUsername());
            return ResponseEntity.ok(ticket);

        } catch (IllegalArgumentException ex) {
            logger.error("Activation failed: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error during activation: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("An unexpected error occurred. Please try again later.");
        }
    }
}
