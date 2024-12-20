package ru.mtuci.Dubovikov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.Dubovikov.model.ApplicationUser;
import ru.mtuci.Dubovikov.model.Device;
import ru.mtuci.Dubovikov.model.DeviceInfoRequest;
import ru.mtuci.Dubovikov.service.DeviceService;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping("/register")
    public ResponseEntity<Device> registerDevice(@RequestBody DeviceInfoRequest deviceInfo,
                                                 @AuthenticationPrincipal ApplicationUser currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Device device = deviceService.registerOrUpdateDevice(deviceInfo.getMacAddress(), currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(device);
    }

    @GetMapping("/find")
    public ResponseEntity<Device> findDevice(@RequestBody DeviceInfoRequest deviceInfo,
                                             @AuthenticationPrincipal ApplicationUser currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Device device = deviceService.findDeviceByInfo(deviceInfo, currentUser);
        if (device == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(device);
    }
}
