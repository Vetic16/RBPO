package ru.mtuci.rbpo_2024_praktika.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
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

    @ManyToOne
    @JoinColumn(name = "license_id", nullable = false)
    private License license;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "activation_date")
    @Temporal(TemporalType.DATE)
    private Date activationDate;

    @OneToMany(mappedBy = "deviceLicense", cascade = CascadeType.ALL)
    private List<Details> details;
}
