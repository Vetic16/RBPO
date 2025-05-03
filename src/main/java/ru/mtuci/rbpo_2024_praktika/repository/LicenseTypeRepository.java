package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_2024_praktika.model.LicenseType;

public interface LicenseTypeRepository extends JpaRepository<LicenseType, Long> {
    // JpaRepository уже предоставляет метод findById
}