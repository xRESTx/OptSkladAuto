package com.warehouse.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Fuel_Supply")
public class FuelSupply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supply_id")
    private int supplyId;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne
    @JoinColumn(name = "fuel_type_id")
    private FuelType fuelType;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "quantity_liters")
    private double quantityLiters;

    @Column(name = "supply_date")
    private Date supplyDate;

    // Getters and Setters

    public int getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(int supplyId) {
        this.supplyId = supplyId;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public double getQuantityLiters() {
        return quantityLiters;
    }

    public void setQuantityLiters(double quantityLiters) {
        this.quantityLiters = quantityLiters;
    }

    public Date getSupplyDate() {
        return supplyDate;
    }

    public void setSupplyDate(Date supplyDate) {
        this.supplyDate = supplyDate;
    }
}
