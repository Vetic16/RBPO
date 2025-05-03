package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_2024_praktika.model.SignatureHistory;

import java.util.List;
import java.util.UUID;

public interface SignatureHistoryRepository extends JpaRepository<SignatureHistory, Long> {

    // Метод для получения всех записей по signature_id (например, для поиска истории изменений сигнатуры)
    List<SignatureHistory> findBySignatureId(UUID signatureId);

    // Метод для получения всех историй для конкретной сигнатуры по версии (можно использовать для поиска всех версий сигнатуры)
    List<SignatureHistory> findBySignatureIdOrderByVersionCreatedAtDesc(UUID signatureId);

    // Метод для получения всех записей в истории по статусу
    List<SignatureHistory> findByStatus(String status);

    // Метод для получения всех записей по статусу и сигнатуре
    List<SignatureHistory> findBySignatureIdAndStatus(UUID signatureId, String status);
}
