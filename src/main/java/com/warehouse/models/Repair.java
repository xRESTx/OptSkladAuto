package com.warehouse.models;

import javax.persistence.*;

@Entity
@Table(name = "repair")
public class Repair {

    @Id
    @Column(name = "articul")
    private int articul;

    @Column(name = "subcategory")
    private String subcategory;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "oem")
    private int oem;

    @Column(name = "material")
    private String material;

    @Column(name = "size")
    private String size;

    @Column(name = "weight")
    private String weight;

    @Column(name = "compatibility")
    private String compatibility;

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

    public int getOem() {
        return oem;
    }

    public void setOem(int oem) {
        this.oem = oem;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCompatibility() {
        return compatibility;
    }

    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }

    public Autotovar getAutotovar() {
        return autotovar;
    }

    public void setAutotovar(Autotovar autotovar) {
        this.autotovar = autotovar;
    }
}
