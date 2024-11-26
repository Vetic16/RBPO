package ru.mtuci.Dubovikov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mtuci.Dubovikov.model.Device;
import ru.mtuci.Dubovikov.model.License;
import ru.mtuci.Dubovikov.model.ApplicationUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {

    // Поиск лицензии по коду активации
    Optional<License> findByCode(String code);

    // Поиск всех лицензий по объекту владельца
    List<License> findAllByOwner(ApplicationUser owner);

    List<License> findAllByDeviceAndOwnerAndBlockedFalse(Device device, ApplicationUser owner);

    // Поиск всех лицензий по идентификатору владельца через @Query
    @Query("SELECT l FROM License l WHERE l.owner.id = :ownerId")
    List<License> findAllByOwnerId(@Param("ownerId") Long ownerId);

    // Поиск всех лицензий по идентификатору владельца и устройства через @Query
    @Query("SELECT l FROM License l WHERE l.owner.id = :ownerId AND l.device.id = :deviceId")
    List<License> findAllByOwnerIdAndDeviceId(@Param("ownerId") Long ownerId, @Param("deviceId") Long deviceId);
}
