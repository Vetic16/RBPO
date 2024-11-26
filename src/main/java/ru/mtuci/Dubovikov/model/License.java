package ru.mtuci.Dubovikov.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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

    @Column(name = "code", nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private LicenseType type;

    @Column(name = "first_activation_date")
    @Temporal(TemporalType.DATE)
    private LocalDate firstActivationDate;

    @Column(name = "ending_date")
    @Temporal(TemporalType.DATE)
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

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }
}
