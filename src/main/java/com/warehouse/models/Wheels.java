package com.warehouse.models;

import javax.persistence.*;

@Entity
@Table(name = "wheels")
public class Wheels {

    @Id
    @Column(name = "articul")
    private int articul;

    @Column(name = "subcategory")
    private String subcategory;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "size")
    private String size;

    @Column(name = "protector")
    private String protector;

    @Column(name = "material")
    private String material;

    @Column(name = "colour")
    private String colour;

    @Column(name = "seasonality")
    private String seasonality;

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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getProtector() {
        return protector;
    }

    public void setProtector(String protector) {
        this.protector = protector;
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

    public String getSeasonality() {
        return seasonality;
    }

    public void setSeasonality(String seasonality) {
        this.seasonality = seasonality;
    }

    public Autotovar getAutotovar() {
        return autotovar;
    }

    public void setAutotovar(Autotovar autotovar) {
        this.autotovar = autotovar;
    }
}
