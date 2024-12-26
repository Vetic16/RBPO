package ru.mtuci.rbpo_2024_praktika.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.model.License;
import ru.mtuci.rbpo_2024_praktika.model.LicenseHistory;
import ru.mtuci.rbpo_2024_praktika.repository.LicenseHistoryRepository;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class LicenseHistoryService {

    private final LicenseHistoryRepository licenseHistoryRepository;

    public void recordLicenseChange(License license, ApplicationUser user, String status, String description) {
        LicenseHistory history = new LicenseHistory();
        history.setLicense(license);
        history.setUser(user);
        history.setStatus(status);
        history.setDescription(description);
        history.setChangeDate(new Date());
        history.setLicenseId(license.getId());
        history.setUserId(user.getId());
        history.setStatus(status);
        history.setChangeDate(new java.util.Date());
        history.setDescription(description);

        licenseHistoryRepository.save(history);

        licenseHistoryRepository.save(history);
    }
}
