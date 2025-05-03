package ru.mtuci.rbpo_2024_praktika.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo_2024_praktika.model.Signature;
import ru.mtuci.rbpo_2024_praktika.model.Signature.SignatureStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SignatureRepository extends JpaRepository<Signature, UUID> {

    // Возвращает список актуальных сигнатур, обновленных после указанной даты
    List<Signature> findByUpdatedAtAfterAndStatus(LocalDateTime updatedAt, Signature.SignatureStatus status);

    // Возвращает список сигнатур по списку id и статусу
    List<Signature> findByIdInAndStatus(List<UUID> ids, Signature.SignatureStatus status);

    // Возвращает все сигнатуры, в том числе со статусом DELETED (для "диффа")
    List<Signature> findByStatus(Signature.SignatureStatus status);

    // Возвращает список актуальных сигнатур (не DELETED) по указанным id
    List<Signature> findByIdInAndStatusNot(List<UUID> ids, Signature.SignatureStatus status);

    // Возвращает список актуальных сигнатур по статусу, исключая DELETED
    List<Signature> findByStatusNot(Signature.SignatureStatus status);

    // Возвращает список всех записей (включая DELETED) по статусу
    List<Signature> findByStatusIn(List<SignatureStatus> statuses);

    // Метод для получения всех записей по id
    List<Signature> findByIdIn(List<UUID> ids);

    // Метод для получения актуальных сигнатур по определенному статусу
    List<Signature> findByStatusNotAndIdIn(SignatureStatus status, List<UUID> ids);
}
