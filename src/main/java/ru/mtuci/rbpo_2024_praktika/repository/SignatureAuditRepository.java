package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_2024_praktika.model.ApplicationUser;
import ru.mtuci.rbpo_2024_praktika.model.SignatureAudit;

import java.util.List;
import java.util.UUID;

public interface SignatureAuditRepository extends JpaRepository<SignatureAudit, UUID> {

    // Возвращает список аудитов по записи
    List<SignatureAudit> findBySignatureId(UUID signatureId);

    // Возвращает список аудитов по пользователю (для анализа, кто что изменял)
    List<SignatureAudit> findByChangedBy(ApplicationUser changedBy);


    // Получаем все записи аудита для администратора
    List<SignatureAudit> findAllByOrderByChangedAtDesc();
}
