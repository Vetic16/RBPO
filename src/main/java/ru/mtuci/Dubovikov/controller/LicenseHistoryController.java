package ru.mtuci.Dubovikov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.Dubovikov.model.LicenseHistory;
import ru.mtuci.Dubovikov.repository.LicenseHistoryRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/license-history")
@RequiredArgsConstructor
public class LicenseHistoryController {

    private final LicenseHistoryRepository licenseHistoryRepository;

    // Получить все записи истории лицензий
    @GetMapping
    public ResponseEntity<List<LicenseHistory>> getAllLicenseHistories() {
        List<LicenseHistory> histories = licenseHistoryRepository.findAll();
        if (histories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(histories);
    }

    // Получить запись истории лицензии по ID
    @GetMapping("/{id}")
    public ResponseEntity<LicenseHistory> getLicenseHistoryById(@PathVariable Long id) {
        Optional<LicenseHistory> history = licenseHistoryRepository.findById(id);
        return history.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Добавить новую запись истории лицензии
    @PostMapping
    public ResponseEntity<LicenseHistory> addLicenseHistory(@RequestBody LicenseHistory licenseHistory) {
        if (licenseHistory.getLicense() == null || licenseHistory.getUser() == null || licenseHistory.getStatus() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        licenseHistory.setChangeDate(new Date()); // Устанавливаем текущую дату
        LicenseHistory savedHistory = licenseHistoryRepository.save(licenseHistory);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHistory);
    }

    // Обновить существующую запись истории лицензии
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
        history.setChangeDate(new Date()); // Обновляем дату изменения
        history.setDescription(updatedHistory.getDescription());

        LicenseHistory savedHistory = licenseHistoryRepository.save(history);
        return ResponseEntity.ok(savedHistory);
    }

    // Удалить запись истории лицензии
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLicenseHistory(@PathVariable Long id) {
        if (!licenseHistoryRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        licenseHistoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
