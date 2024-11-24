package com.warehouse.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "autotovar")
public class Autotovar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "articul")
    private int articul;

    @Column(name = "category")
    private String category;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "cost", precision = 10, scale = 2)
    private double cost;

    @Column(name = "minimum", nullable = false)
    private int minimum;

    @Column(name = "available")
    private boolean available;

    @Column(name = "remaining_stock", nullable = false)
    private int remainingStock;

    @OneToMany(mappedBy = "autotovar", cascade = CascadeType.ALL)
    private List<OrderComponent> orderComponents;

    // Getters and Setters

    public int getArticul() {
        return articul;
    }

    public void setArticul(int articul) {
        this.articul = articul;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getRemainingStock() {
        return remainingStock;
    }

    public void setRemainingStock(int remainingStock) {
        this.remainingStock = remainingStock;
    }

    public List<OrderComponent> getOrderComponents() {
        return orderComponents;
    }

    public void setOrderComponents(List<OrderComponent> orderComponents) {
        this.orderComponents = orderComponents;
    }
}
