package com.warehouse.page;
import com.warehouse.page.adminPage.*;
import javax.swing.*;
import java.awt.*;

public class AdminScreen {

    public static void showAdminScreen(String adminName) {
        JFrame frame = new JFrame("Admin Dashboard");
        frame.setSize(600, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок приветствия
        JLabel adminLabel = new JLabel("Welcome, " + adminName + "!");
        adminLabel.setHorizontalAlignment(SwingConstants.CENTER);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(adminLabel, BorderLayout.NORTH);

        // Панель кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(13, 1, 10, 10));

        // Создаем кнопки для каждой таблицы
        JButton accountButton = new JButton("Account");
        accountButton.addActionListener(e -> {
            AccountPage.showAccountPage();
        });
        buttonPanel.add(accountButton);

        JButton clientsButton = new JButton("Clients");
        clientsButton.addActionListener(e -> {
            ClientsPage.showClientsPage();
        });
        buttonPanel.add(clientsButton);

        JButton positionsButton = new JButton("Positions");
        positionsButton.addActionListener(e -> {
            PositionsPage.showPositionsPage();
        });
        buttonPanel.add(positionsButton);

        JButton employeesButton = new JButton("Employees");
        employeesButton.addActionListener(e -> {
            EmployeesPage.showEmployeesPage();
        });
        buttonPanel.add(employeesButton);

        JButton contractsButton = new JButton("Contracts");
        contractsButton.addActionListener(e -> {
            ContractsPage.showContractsPage();
        });
        buttonPanel.add(contractsButton);
//
//        JButton ordersButton = new JButton("Orders");
//        ordersButton.addActionListener(e -> {
//            OrdersPage.showOrdersPage();
//        });
//        buttonPanel.add(ordersButton);
//
//        JButton requestsButton = new JButton("Requests");
//        requestsButton.addActionListener(e -> {
//            RequestsPage.showRequestsPage();
//        });
//        buttonPanel.add(requestsButton);
//
//        JButton departmentsButton = new JButton("Departments");
//        departmentsButton.addActionListener(e -> {
//            DepartmentsPage.showDepartmentsPage();
//        });
//        buttonPanel.add(departmentsButton);
//
//        JButton productsButton = new JButton("Products");
//        productsButton.addActionListener(e -> {
//            ProductsPage.showProductsPage();
//        });
//        buttonPanel.add(productsButton);
//
//        JButton paymentsButton = new JButton("Payments");
//        paymentsButton.addActionListener(e -> {
//            PaymentsPage.showPaymentsPage();
//        });
//        buttonPanel.add(paymentsButton);
//
//        JButton orderItemsButton = new JButton("Order Items");
//        orderItemsButton.addActionListener(e -> {
//            OrderItemsPage.showOrderItemsPage();
//        });
//        buttonPanel.add(orderItemsButton);
//
//        JButton suppliersButton = new JButton("Suppliers");
//        suppliersButton.addActionListener(e -> {
//            SuppliersPage.showSuppliersPage();
//        });
//        buttonPanel.add(suppliersButton);
//
//        JButton suppliesButton = new JButton("Supplies");
//        suppliesButton.addActionListener(e -> {
//            SuppliesPage.showSuppliesPage();
//        });
//        buttonPanel.add(suppliesButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
