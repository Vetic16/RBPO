package ru.mtuci.rbpo_2024_praktika.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.model.Device;
import ru.mtuci.rbpo_2024_praktika.model.DeviceInfoRequest;
import ru.mtuci.rbpo_2024_praktika.service.ApplicationUserService;
import ru.mtuci.rbpo_2024_praktika.service.DeviceService;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationRole;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final ApplicationUserService applicationUserService;

    @PostMapping("/register")
    public ResponseEntity<?> registerDevice(@RequestBody DeviceInfoRequest deviceInfo,
                                            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Please log in.");
        }

        String email = currentUser.getUsername();
        ApplicationUser applicationUser = applicationUserService.findByEmail(email);
        if (applicationUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
        }

        try {
            boolean isAdmin = applicationUser.getRole() == ApplicationRole.ADMIN;


            Device device = deviceService.registerOrUpdateDevice(
                    deviceInfo.getMacAddress(),
                    deviceInfo.getDeviceName(),
                    applicationUser,
                    isAdmin
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(device);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + ex.getMessage());
        }
    }

    @GetMapping("/find")
    public ResponseEntity<Device> findDevice(@RequestBody DeviceInfoRequest deviceInfo,
                                             @AuthenticationPrincipal User currentUser) { // Исправлено
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


        String email = currentUser.getUsername();


        ApplicationUser applicationUser = applicationUserService.findByEmail(email);
        if (applicationUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Device device = deviceService.findDeviceByInfo(deviceInfo, applicationUser);
        if (device == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(device);
    }
}
