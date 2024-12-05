package com.warehouse.entity;

import javax.persistence.*;

@Entity
@Table(name = "Должности")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_должности")
    private Long id;

    @Column(name = "наименование", nullable = false, length = 50)
    private String name;

    @Column(name = "описание", columnDefinition = "TEXT")
    private String description;

    // Конструкторы, геттеры и сеттеры

    public Position() {}

    public Position(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
