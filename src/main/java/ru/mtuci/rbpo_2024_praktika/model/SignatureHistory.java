package ru.mtuci.rbpo_2024_praktika.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class SignatureHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "signature_id", nullable = false)
    private Signature signature;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date versionCreatedAt;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private String threatName;

    @Lob
    @Column(nullable = false, length = 8)
    private byte[] firstBytes;

    @Column(nullable = false)
    private String remainderHash;

    @Column(nullable = false)
    private int remainderLength;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private int offsetStart;

    @Column(nullable = false)
    private int offsetEnd;

    @Lob
    @Column(nullable = false)
    private byte[] digitalSignature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Signature.SignatureStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "modified_by", nullable = false)
    private ApplicationUser modifiedBy;

    @PrePersist
    private void preSave() {
        versionCreatedAt = new Date();
    }
}
