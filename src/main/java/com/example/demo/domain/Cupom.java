package com.example.demo.domain;

import com.example.demo.domain.exceptions.CoupomAlreadyDeletedException;
import com.example.demo.domain.exceptions.InvalidCupomException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Cupom {
    private Long id;
    private Long version;
    private CodeCupom code;
    private String description;
    private Discount discountValue;
    private LocalDateTime expirationDate;
    private boolean published;
    private LocalDateTime deletedAt;


    public Cupom(String code, String description, BigDecimal discountValue, LocalDateTime expirationDate, boolean published){
        this.code = new CodeCupom(code);
        this.description = this.validateDescription(description);
        this.discountValue = new Discount(discountValue);
        this.expirationDate = this.validateExpirationDate(expirationDate);
        this.published = published;
        this.deletedAt = null;
        this.version = 0L ;
    }

    public Cupom(String code, String description, BigDecimal discountValue, LocalDateTime expirationDate, boolean published, LocalDateTime deletedAt, Long version) {
        this.code = new CodeCupom(code);
        this.description = this.validateDescription(description);
        this.discountValue = new Discount(discountValue);
        this.expirationDate = this.validateExpirationDate(expirationDate);
        this.published = published;
        this.deletedAt = deletedAt;
        this.version = version ;
    }

    private String validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new InvalidCupomException("Descrição não pode ser nula");
        }
        return description;
    }

    private LocalDateTime validateExpirationDate(LocalDateTime expirationDate) {
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw new InvalidCupomException("Data de expiração não pode ser no passado");
        }
        return expirationDate;
    }

    public void delete() {
        if (isDeleted()) {
            throw new CoupomAlreadyDeletedException("Cupom já foi deletado");
        }
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public Long getId() { return id; }
    public String getCode() { return code.getValue(); }
    public String getDescription() { return description; }
    public BigDecimal getDiscountValue() { return discountValue.getValue(); }
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public boolean isPublished() { return published; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public Long getVersion() { return version; }



    // Usado pelo repository para reconstruir do banco
    public void setId(Long id) { this.id = id; }
    public void setVersion(Long version) { this.version = version; }

}
