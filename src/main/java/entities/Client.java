package com.warehouse.entity;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Table(name = "Клиенты")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_клиента")
    private Long id;

    @Column(name = "фио", nullable = false, length = 100)
    private String fullName;

    @Column(name = "номер_телефона", length = 15)
    private String phoneNumber;

    @Column(name = "электронная_почта", length = 100)
    private String email;

    @Column(name = "адрес", columnDefinition = "TEXT")
    private String address;

    @ManyToOne
    @JoinColumn(name = "id_учетной_записи", nullable = false)
    private Account account; // Ссылка на сущность Account, которая представляет учетную запись

    // Конструкторы, геттеры и сеттеры

    public Client() {}

    public Client(String fullName, String phoneNumber, String email, String address, Account account) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
