package com.warehouse.models;

import javax.persistence.*;

@Entity
@Table(name = "FuelTypes")
public class FuelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fuel_type_id")
    private int fuelTypeId;

    @Column(name = "name")
    private String name;

    @Column(name = "price_per_liter")
    private double pricePerLiter;

    // Getters and Setters

    public int getFuelTypeId() {
        return fuelTypeId;
    }

    public void setFuelTypeId(int fuelTypeId) {
        this.fuelTypeId = fuelTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricePerLiter() {
        return pricePerLiter;
    }

    public void setPricePerLiter(double pricePerLiter) {
        this.pricePerLiter = pricePerLiter;
    }
}
