package com.warehouse.models;

import javax.persistence.*;

@Entity
@Table(name = "order_component")
public class OrderComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "number_order", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "articul", nullable = false)
    private Autotovar autotovar;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "summa", precision = 10, scale = 2)
    private double summa;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Order getOrders() {
        return order;
    }

    public void setOrders(Order orders) {
        this.order = orders;
    }

    public Autotovar getAutotovar() {
        return autotovar;
    }

    public void setAutotovar(Autotovar autotovar) {
        this.autotovar = autotovar;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getSumma() {
        return summa;
    }

    public void setSumma(double summa) {
        this.summa = summa;
    }
}
