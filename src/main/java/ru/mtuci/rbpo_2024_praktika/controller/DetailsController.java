package ru.mtuci.rbpo_2024_praktika.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbpo_2024_praktika.model.DeviceLicense;
import ru.mtuci.rbpo_2024_praktika.model.Details;
import ru.mtuci.rbpo_2024_praktika.repository.DetailsRepository;
import ru.mtuci.rbpo_2024_praktika.service.DeviceLicenseService;
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

        DeviceLicense deviceLicense = deviceLicenseService.findById(deviceLicenseId);


        if (deviceLicense == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("DeviceLicense с ID " + deviceLicenseId + " не найден.");
        }


        details.setDeviceLicense(deviceLicense);


        detailsRepository.save(details);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Details успешно сохранены.");
    }
}