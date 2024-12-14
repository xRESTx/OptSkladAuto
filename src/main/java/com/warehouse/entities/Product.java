package com.warehouse.entities;

import javax.persistence.*;

@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity = 0; // Значение по умолчанию для соответствия DEFAULT 0 в БД

    @Column(name = "expiration_date_month")
    private Integer expiryDate; // Сделано Integer, чтобы поддерживать null-значения

    @Column(name = "description")
    private String description;

    // Конструктор по умолчанию (нужен для Hibernate)
    public Product() {
    }

    // Конструктор для создания нового продукта (без ID)
    public Product(String name, Department department, double price, Integer expiryDate, String description) {
        this.name = name;
        this.department = department;
        this.price = price;
        this.stockQuantity = 0; // Устанавливаем начальное количество
        this.expiryDate = expiryDate;
        this.description = description;
    }

    // Конструктор со всеми параметрами (например, для загрузки из БД)
    public Product(int id, String name, Department department, double price, int stockQuantity, Integer expiryDate, String description) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.expiryDate = expiryDate;
        this.description = description;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Integer expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
