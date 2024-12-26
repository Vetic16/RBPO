package ru.mtuci.rbpo_2024_praktika.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbpo_2024_praktika.model.LicenseType;
import ru.mtuci.rbpo_2024_praktika.repository.LicenseTypeRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/license-types")
@RequiredArgsConstructor
public class LicenseTypeController {

    private final LicenseTypeRepository licenseTypeRepository;

    @GetMapping
    public ResponseEntity<List<LicenseType>> getAllLicenseTypes() {
        List<LicenseType> licenseTypes = licenseTypeRepository.findAll();
        if (licenseTypes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(licenseTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LicenseType> getLicenseTypeById(@PathVariable Long id) {
        Optional<LicenseType> licenseType = licenseTypeRepository.findById(id);
        return licenseType.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<LicenseType> addLicenseType(@RequestBody LicenseType licenseType) {
        if (licenseType.getName() == null || licenseType.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        LicenseType savedLicenseType = licenseTypeRepository.save(licenseType);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLicenseType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LicenseType> updateLicenseType(@PathVariable Long id, @RequestBody LicenseType updatedLicenseType) {
        Optional<LicenseType> existingLicenseType = licenseTypeRepository.findById(id);

        if (existingLicenseType.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        LicenseType licenseType = existingLicenseType.get();
        licenseType.setName(updatedLicenseType.getName());
        licenseType.setDefaultDuration(updatedLicenseType.getDefaultDuration());
        licenseType.setDescription(updatedLicenseType.getDescription());

        LicenseType savedLicenseType = licenseTypeRepository.save(licenseType);
        return ResponseEntity.ok(savedLicenseType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLicenseType(@PathVariable Long id) {
        if (!licenseTypeRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        licenseTypeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
