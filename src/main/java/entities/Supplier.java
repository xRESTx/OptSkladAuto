package com.warehouse.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Поставщики")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_поставщика")
    private Long id;

    @Column(name = "наименование", nullable = false)
    private String name;

    @Column(name = "контактный_телефон")
    private String phone;

    @Column(name = "электронная_почта")
    private String email;

    @Column(name = "контактное_лицо", nullable = false)
    private String contactPerson;

    @Column(name = "дата_начала_сотрудничества", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    // Конструкторы, геттеры и сеттеры

    public Supplier() {}

    public Supplier(String name, String phone, String email, String contactPerson, Date startDate) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.contactPerson = contactPerson;
        this.startDate = startDate;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
