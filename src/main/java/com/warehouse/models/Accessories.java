package com.warehouse.models;

import javax.persistence.*;

@Entity
@Table(name = "accessories")
public class Accessories {

    @Id
    @Column(name = "articul")
    private int articul;

    @Column(name = "subcategory")
    private String subcategory;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "features")
    private String features;

    @Column(name = "appointment")
    private String appointment;

    @Column(name = "size")
    private String size;

    @Column(name = "material")
    private String material;

    @Column(name = "colour")
    private String colour;

    @OneToOne
    @JoinColumn(name = "articul", insertable = false, updatable = false)
    private Autotovar autotovar;

    // Getters and Setters


    public int getArticul() {
        return articul;
    }

    public void setArticul(int articul) {
        this.articul = articul;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getAppointment() {
        return appointment;
    }

    public void setAppointment(String appointment) {
        this.appointment = appointment;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Autotovar getAutotovar() {
        return autotovar;
    }

    public void setAutotovar(Autotovar autotovar) {
        this.autotovar = autotovar;
    }
}
