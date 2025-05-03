package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.rbpo_2024_praktika.model.Device;
import ru.mtuci.rbpo_2024_praktika.model.DeviceLicense;

import java.util.List;

@Repository
public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {
    List<DeviceLicense> findDeviceLicenseByDevice(Device device);

    int countByLicenseId(Long licenseId);

    List<DeviceLicense> findAllByLicenseId(Long licenseId);
}
