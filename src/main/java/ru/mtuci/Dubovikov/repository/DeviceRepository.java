package ru.mtuci.Dubovikov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.Dubovikov.model.ApplicationUser;
import ru.mtuci.Dubovikov.model.Device;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    // Поиск устройства по MAC-адресу
    Optional<Device> findByMacAddress(String macAddress);
    Optional<Device> findByMacAddressAndUser(String macAddress, ApplicationUser user);
}
