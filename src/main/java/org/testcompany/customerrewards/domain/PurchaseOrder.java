package org.testcompany.customerrewards.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class PurchaseOrder implements Serializable {
    @Id
    private Long id;
    @ManyToOne
    private Customer customer;
    @Column(precision = 15, scale = 2)
    private BigDecimal transactionAmount;
    private Instant transactionDate;

    public PurchaseOrder(Long id, Customer customer, BigDecimal transactionAmount, Instant transactionDate) {
        this.id = id;
        this.customer = customer;
        this.transactionAmount = transactionAmount;
        this.transactionDate = transactionDate;
    }

    public PurchaseOrder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
