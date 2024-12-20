package ru.mtuci.Dubovikov.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "license_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LicenseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "license_id", nullable = false)
    private License license;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "change_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date changeDate;

    @Column(name = "description")
    private String description;

    // Добавленный метод
    public void setLicenseId(Long licenseId) {
        if (this.license == null) {
            this.license = new License();
        }
        this.license.setId(licenseId);
    }

    // Добавленный метод
    public void setUserId(Long userId) {
        if (this.user == null) {
            this.user = new ApplicationUser();
        }
        this.user.setId(userId);
    }
}
