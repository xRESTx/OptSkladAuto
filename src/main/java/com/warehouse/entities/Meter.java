package com.warehouse.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Meters")
public class Meter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meter_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(name = "installation_date", nullable = false)
    private LocalDate installationDate;

    @Column(name = "last_reading_date")
    private LocalDate lastReadingDate;

    @Column(name = "last_reading_value")
    private Double lastReadingValue;

    public Meter() {
    }

    public Meter(Client client, Service service, LocalDate installationDate) {
        this.client = client;
        this.service = service;
        this.installationDate = installationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    public LocalDate getLastReadingDate() {
        return lastReadingDate;
    }

    public void setLastReadingDate(LocalDate lastReadingDate) {
        this.lastReadingDate = lastReadingDate;
    }

    public Double getLastReadingValue() {
        return lastReadingValue;
    }

    public void setLastReadingValue(Double lastReadingValue) {
        this.lastReadingValue = lastReadingValue;
    }
}
