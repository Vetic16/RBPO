package ru.mtuci.rbpo_2024_praktika.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class SignatureAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @ManyToOne
    @JoinColumn(name = "signature_id", nullable = false)
    private Signature signature;

    @ManyToOne
    @JoinColumn(name = "changed_by", nullable = false)
    private ApplicationUser changedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditChangeType changeType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date changedAt;

    @Lob
    @Column(nullable = false)
    private String fieldsChanged;
}