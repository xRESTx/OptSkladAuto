package com.warehouse.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Meter_Readings")
public class MeterReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reading_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @Column(name = "reading_date", nullable = false)
    private LocalDate readingDate;

    @Column(name = "reading_value", nullable = false)
    private double readingValue;

    public MeterReading() {
    }

    public MeterReading(Meter meter, LocalDate readingDate, double readingValue) {
        this.meter = meter;
        this.readingDate = readingDate;
        this.readingValue = readingValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getReadingValue() {
        return readingValue;
    }

    public void setReadingValue(double readingValue) {
        this.readingValue = readingValue;
    }

    public LocalDate getReadingDate() {
        return readingDate;
    }

    public void setReadingDate(LocalDate readingDate) {
        this.readingDate = readingDate;
    }

    public Meter getMeter() {
        return meter;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }
}
