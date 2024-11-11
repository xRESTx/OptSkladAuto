package com.warehouse.entity;

import javax.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "articul")  // ?????????, ??? ???? articul ????? ??????????????? ??????
    private Long articul;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private double price;

    @Column(name = "category")
    private String category;

    @Column(name = "description")
    private String description;

    @Column(name = "nalichie")
    private int nalichie;  // ?????????? ?????? ?? ??????

    // ?????? ??????????? (????????? ??? Hibernate)
    public Product() {}

    // ??????????? ??? ????????????? ???????
    public Product(String name, double price, String category, String description, int nalichie) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.nalichie = nalichie;
    }

    // ??????? ? ??????? ??? ???? ?????

    public Long getArticul() {
        return articul;
    }

    public void setArticul(Long articul) {
        this.articul = articul;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNalichie() {
        return nalichie;
    }

    public void setNalichie(int nalichie) {
        this.nalichie = nalichie;
    }

    @Override
    public String toString() {
        return "Product{" +
                "articul=" + articul +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", nalichie=" + nalichie +
                '}';
    }
}
