package com.warehouse.models;

import javax.persistence.*;

@Entity
@Table(name = "electronics")
public class Electronics {

    @Id
    @Column(name = "articul")
    private int articul;

    @Column(name = "subcategory")
    private String subcategory;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "supported")
    private String supported;

    @Column(name = "warranty")
    private String warranty;

    @Column(name = "permission")
    private String permission;

    @Column(name = "connect")
    private String connect;

    @Column(name = "screen")
    private String screen;

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

    public String getSupported() {
        return supported;
    }

    public void setSupported(String supported) {
        this.supported = supported;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public Autotovar getAutotovar() {
        return autotovar;
    }

    public void setAutotovar(Autotovar autotovar) {
        this.autotovar = autotovar;
    }
}
