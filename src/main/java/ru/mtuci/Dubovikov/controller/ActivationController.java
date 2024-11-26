package ru.mtuci.Dubovikov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.Dubovikov.model.ApplicationUser;
import ru.mtuci.Dubovikov.model.Device;
import ru.mtuci.Dubovikov.model.Ticket;
import ru.mtuci.Dubovikov.service.DeviceService;
import ru.mtuci.Dubovikov.service.LicenseService;

@RestController
@RequestMapping("/activation")
@RequiredArgsConstructor
public class ActivationController {
    @Lazy
    private final LicenseService licenseService;
    private final DeviceService deviceService;

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(
            @RequestParam String activationCode,
            @RequestParam String deviceInfo) {

        // Получаем текущего пользователя
        ApplicationUser authenticatedUser = (ApplicationUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        try {
            // Регистрируем или обновляем устройство
            Device device = deviceService.registerOrUpdateDevice(deviceInfo, authenticatedUser);

            // Активация лицензии
            Ticket ticket = licenseService.activateLicense(activationCode, device, authenticatedUser);

            // Возвращаем тикет
            return ResponseEntity.ok(ticket);
        } catch (IllegalArgumentException ex) {
            // Ошибки активации
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
