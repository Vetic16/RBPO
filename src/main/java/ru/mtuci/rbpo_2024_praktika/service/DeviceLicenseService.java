package ru.mtuci.rbpo_2024_praktika.service;

import ru.mtuci.rbpo_2024_praktika.model.DeviceLicense;

import java.util.List;

public interface DeviceLicenseService {

    void save(DeviceLicense deviceLicense);

    List<DeviceLicense> findAll();

    DeviceLicense findById(long id);

    boolean update(long id, DeviceLicense updatedDeviceLicense);

    boolean delete(long id);

}
