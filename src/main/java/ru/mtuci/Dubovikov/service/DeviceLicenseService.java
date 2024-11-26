package ru.mtuci.Dubovikov.service;

import ru.mtuci.Dubovikov.model.DeviceLicense;
import java.util.List;

public interface DeviceLicenseService {
    void save(DeviceLicense deviceLicense);
    List<DeviceLicense> findAll();
    DeviceLicense findById(long id);
}
