package ru.mtuci.rbpo_2024_praktika.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Signature {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Уникальный идентификатор записи (UUID)

    @Column(nullable = false)
    private String threatName; // Название угрозы

    @Column(nullable = false, length = 8)
    private byte[] firstBytes; // Первые 8 байт сигнатуры

    @Column(nullable = false)
    private String remainderHash; // Хэш от "хвоста"

    @Column(nullable = false)
    private int remainderLength; // Количество байт в "хвосте"

    @Column(nullable = false)
    private String fileType; // Тип файла, для которого актуальна сигнатура

    @Column(nullable = false)
    private int offsetStart; // Смещение начала сигнатуры в файле

    @Column(nullable = false)
    private int offsetEnd; // Смещение конца сигнатуры в файле

    @Lob
    @Column(nullable = false)
    private byte[] digitalSignature; // Электронная цифровая подпись

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt; // Дата и время последнего обновления

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SignatureStatus status; // Статус записи

    @Column(nullable = false)
    private Integer version; // Версия записи

    @Column(nullable = false)
    private String modifiedBy; // Пользователь, который изменил запись

    public enum SignatureStatus {
        ACTUAL("ACTUAL"),
        DELETED("DELETED"),
        CORRUPTED("CORRUPTED");

        private final String value;

        SignatureStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @PrePersist
    @PreUpdate
    private void preSave() {
        if (version == null) {
            version = 1; // При создании записи версия = 1
        } else {
            version++; // При обновлении увеличиваем версию
        }
    }
}
