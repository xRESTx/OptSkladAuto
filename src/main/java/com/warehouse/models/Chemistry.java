package com.warehouse.models;

import javax.persistence.*;

@Entity
@Table(name = "chemistry")
public class Chemistry {

    @Id
    @Column(name = "articul")
    private int articul;

    @Column(name = "subcategory")
    private String subcategory;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "compositions")
    private String compositions;

    @Column(name = "concentration")
    private String concentration;

    @Column(name = "target")
    private String target;

    @Column(name = "volume")
    private String volume;

    @Column(name = "expiration_date")
    private String expirationDate;

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

    public String getCompositions() {
        return compositions;
    }

    public void setCompositions(String compositions) {
        this.compositions = compositions;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Autotovar getAutotovar() {
        return autotovar;
    }

    public void setAutotovar(Autotovar autotovar) {
        this.autotovar = autotovar;
    }
}
