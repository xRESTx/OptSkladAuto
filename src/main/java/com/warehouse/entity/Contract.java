package com.warehouse.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Договоры")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_договора")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_сотрудника", nullable = false)
    private Employee employee;

    @Column(name = "номер_договора", nullable = false, length = 50)
    private String contractNumber;

    @Column(name = "дата_заключения", nullable = false)
    private LocalDate contractDate;

    @Column(name = "Заработная_плата", nullable = false)
    private int salary;

    // Конструкторы, геттеры и сеттеры

    public Contract() {}

    public Contract(Employee employee, String contractNumber, LocalDate contractDate, int salary) {
        this.employee = employee;
        this.contractNumber = contractNumber;
        this.contractDate = contractDate;
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public LocalDate getContractDate() {
        return contractDate;
    }

    public void setContractDate(LocalDate contractDate) {
        this.contractDate = contractDate;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
