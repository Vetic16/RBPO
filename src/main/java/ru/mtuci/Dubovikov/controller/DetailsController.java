package ru.mtuci.Dubovikov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.Dubovikov.model.DeviceLicense;
import ru.mtuci.Dubovikov.model.Details;
import ru.mtuci.Dubovikov.repository.DetailsRepository;
import ru.mtuci.Dubovikov.service.DeviceLicenseService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/details")
@RequiredArgsConstructor
public class DetailsController {

    private final DetailsRepository detailsRepository;
    private final DeviceLicenseService deviceLicenseService;

    @PostMapping("/{device_license_id}/save")
    public ResponseEntity<String> save(@PathVariable(value = "device_license_id") Long deviceLicenseId,
                                       @RequestBody @Valid Details details) {
        // Найдем DeviceLicense по ID
        DeviceLicense deviceLicense = deviceLicenseService.findById(deviceLicenseId);

        // Если не нашли, возвращаем ошибку
        if (deviceLicense == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("DeviceLicense с ID " + deviceLicenseId + " не найден.");
        }

        // Устанавливаем связанный DeviceLicense в Details
        details.setDeviceLicense(deviceLicense);

        // Сохраняем объект Details
        detailsRepository.save(details);

        // Возвращаем успешный ответ
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Details успешно сохранены.");
    }
}