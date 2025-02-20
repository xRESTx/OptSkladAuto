package com.warehouse.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @ManyToOne
    @JoinColumn(name = "tarif_id", nullable = false)
    private Tariff tariff;

    @ManyToOne
    @JoinColumn(name = "reading_id")
    private MeterReading reading;

    @Column(name = "billing_period", nullable = false)
    private LocalDate billingPeriod;

    @Column(name = "status", nullable = false)
    private String status;

    public Invoice() {
    }

    public Invoice(Service service, Tariff tariff, MeterReading reading, LocalDate billingPeriod, String status) {
        this.service = service;
        this.tariff = tariff;
        this.reading = reading;
        this.billingPeriod = billingPeriod;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(LocalDate billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public MeterReading getReading() {
        return reading;
    }

    public void setReading(MeterReading reading) {
        this.reading = reading;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
