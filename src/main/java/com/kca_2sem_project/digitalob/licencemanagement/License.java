package com.kca_2sem_project.digitalob.licencemanagement;

import com.kca_2sem_project.digitalob.utils.EncryptionUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String encryptedValue; // Encrypted license value
    private LocalDateTime createdDatetime;
    private LocalDateTime updatedDatetime;
    private String encryptedExpiryDatetime; // Encrypted expiry date
    private String status; // "ACTIVE" or "INACTIVE"

    @Transient
    public LocalDateTime getExpiryDatetime() {
        // Decrypt the expiry date when accessed
        if (encryptedExpiryDatetime == null) return null;
        String decryptedDate = EncryptionUtil.decrypt(this.encryptedExpiryDatetime);
        return LocalDateTime.parse(decryptedDate);
    }

    public void setExpiryDatetime(LocalDateTime expiryDatetime) {
        // Encrypt the expiry date when set
        this.encryptedExpiryDatetime = EncryptionUtil.encrypt(expiryDatetime.toString());
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDatetime = now;
        this.updatedDatetime = now;
        if (this.encryptedExpiryDatetime == null) {
            setExpiryDatetime(now.plusDays(60));
        }
        this.status = "ACTIVE"; // Default status
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDatetime = LocalDateTime.now();
    }
}
