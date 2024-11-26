package ru.mtuci.Dubovikov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.Dubovikov.model.DeviceLicense;

@Repository
public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {
}
