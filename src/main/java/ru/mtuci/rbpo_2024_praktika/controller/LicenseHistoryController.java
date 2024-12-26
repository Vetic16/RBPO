package ru.mtuci.rbpo_2024_praktika.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbpo_2024_praktika.model.LicenseHistory;
import ru.mtuci.rbpo_2024_praktika.repository.LicenseHistoryRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/license-history")
@RequiredArgsConstructor
public class LicenseHistoryController {

    private final LicenseHistoryRepository licenseHistoryRepository;

    @GetMapping
    public ResponseEntity<List<LicenseHistory>> getAllLicenseHistories() {
        List<LicenseHistory> histories = licenseHistoryRepository.findAll();
        if (histories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(histories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LicenseHistory> getLicenseHistoryById(@PathVariable Long id) {
        Optional<LicenseHistory> history = licenseHistoryRepository.findById(id);
        return history.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<LicenseHistory> addLicenseHistory(@RequestBody LicenseHistory licenseHistory) {
        if (licenseHistory.getLicense() == null || licenseHistory.getUser() == null || licenseHistory.getStatus() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        licenseHistory.setChangeDate(new Date());
        LicenseHistory savedHistory = licenseHistoryRepository.save(licenseHistory);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHistory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LicenseHistory> updateLicenseHistory(@PathVariable Long id, @RequestBody LicenseHistory updatedHistory) {
        Optional<LicenseHistory> existingHistory = licenseHistoryRepository.findById(id);

        if (existingHistory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        LicenseHistory history = existingHistory.get();
        history.setLicense(updatedHistory.getLicense());
        history.setUser(updatedHistory.getUser());
        history.setStatus(updatedHistory.getStatus());
        history.setChangeDate(new Date());
        history.setDescription(updatedHistory.getDescription());

        LicenseHistory savedHistory = licenseHistoryRepository.save(history);
        return ResponseEntity.ok(savedHistory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLicenseHistory(@PathVariable Long id) {
        if (!licenseHistoryRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        licenseHistoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
