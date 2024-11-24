package com.warehouse.models;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "number_order")
    private int numberOrder;

    @Column(name = "date_order")
    @Temporal(TemporalType.DATE)
    private Date dateOrder;

    @Column(name = "status")
    private String status;

    @Column(name = "remark")
    private String remark;

    @Column(name = "total_sum", precision = 10, scale = 2)
    private double totalSum;

    @ManyToOne
    @JoinColumn(name = "login")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "login_employee")
    private Employee employee;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<OrderComponent> orderComponents;

    // Getters and Setters

    public int getNumberOrder() {
        return numberOrder;
    }

    public void setNumberOrder(int numberOrder) {
        this.numberOrder = numberOrder;
    }

    public Date getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(Date dateOrder) {
        this.dateOrder = dateOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public double getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(double totalSum) {
        this.totalSum = totalSum;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public List<OrderComponent> getOrderComponents() {
        return orderComponents;
    }

    public void setOrderComponents(List<OrderComponent> orderComponents) {
        this.orderComponents = orderComponents;
    }
}
