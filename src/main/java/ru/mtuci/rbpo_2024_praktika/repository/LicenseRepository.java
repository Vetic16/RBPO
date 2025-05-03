package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mtuci.rbpo_2024_praktika.model.License;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {

    Optional<License> findByCode(String code);

    @Query("SELECT l FROM License l WHERE l.owner.id = :ownerId")
    List<License> findAllByOwnerId(@Param("ownerId") Long ownerId);

    // Новый метод для получения лицензий, обновленных с последней проверки
    @Query("SELECT l FROM License l WHERE l.updatedAt > :lastCheckDate")
    List<License> findLicensesWithUpdatesSinceLastCheck(@Param("lastCheckDate") LocalDateTime lastCheckDate);
}
