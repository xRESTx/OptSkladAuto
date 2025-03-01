package com.warehouse.models;

import javax.persistence.*;

@Entity
@Table(name = "Fuel_Stock")
public class FuelStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private int stockId;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne
    @JoinColumn(name = "fuel_type_id")
    private FuelType fuelType;

    @Column(name = "quantity_liters")
    private double quantityLiters;

    // Getters and Setters

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
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

    public double getQuantityLiters() {
        return quantityLiters;
    }

    public void setQuantityLiters(double quantityLiters) {
        this.quantityLiters = quantityLiters;
    }
}
