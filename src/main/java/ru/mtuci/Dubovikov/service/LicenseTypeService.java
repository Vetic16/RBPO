package ru.mtuci.Dubovikov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.Dubovikov.model.LicenseType;
import ru.mtuci.Dubovikov.repository.LicenseTypeRepository;

@Service
@RequiredArgsConstructor
public class LicenseTypeService {

    private final LicenseTypeRepository licenseTypeRepository;

    public LicenseType getLicenseTypeById(Long licenseTypeId) {
        return licenseTypeRepository.findById(licenseTypeId).orElse(null);
    }
}
