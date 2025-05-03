package ru.mtuci.rbpo_2024_praktika.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "license")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private LicenseType type;

    @Column(name = "first_activation_date")
    private LocalDate firstActivationDate;

    @Column(name = "ending_date")
    private LocalDate endingDate;

    @Column(name = "blocked", nullable = false)
    private Boolean blocked;

    @Column(name = "device_count")
    private Integer deviceCount;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private ApplicationUser owner;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "description")
    private String description;

    @Column(name = "max_device_count", nullable = false)
    private Integer maxDeviceCount;

    @Column(name = "digital_signature", columnDefinition = "TEXT")
    private String digitalSignature;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }
}
