package ru.mtuci.Dubovikov.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "device_license")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeviceLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Оптимизация загрузки
    @JoinColumn(name = "license_id", nullable = false)
    private License license;

    @ManyToOne(fetch = FetchType.LAZY) // Оптимизация загрузки
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "activation_date", nullable = false)
    private LocalDate activationDate;

    @OneToMany(mappedBy = "deviceLicense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Details> details;

    // Дополнительные методы для удобства
    public void addDetail(Details detail) {
        details.add(detail);
        detail.setDeviceLicense(this);
    }

    public void removeDetail(Details detail) {
        details.remove(detail);
        detail.setDeviceLicense(null);
    }
}
