package ru.mtuci.Dubovikov.service;

import ru.mtuci.Dubovikov.model.DeviceLicense;

import java.util.List;

public interface DeviceLicenseService {

    // Сохранить новую запись
    void save(DeviceLicense deviceLicense);

    // Найти все записи
    List<DeviceLicense> findAll();

    // Найти запись по ID
    DeviceLicense findById(long id);

    // Обновить существующую запись
    boolean update(long id, DeviceLicense updatedDeviceLicense);

    // Удалить запись по ID
    boolean delete(long id);

}
