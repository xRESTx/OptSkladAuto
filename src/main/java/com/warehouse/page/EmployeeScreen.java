package com.warehouse.page;

import com.warehouse.dao.EmployeeDAO;
import com.warehouse.entities.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeScreen {

    public static void showEmployeeScreen(String username) {
        JFrame frame = new JFrame("Employee Screen");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        JLabel userLabel = new JLabel("Welcome, " + username + " (Employee)!");
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Сделать текст крупнее
        mainPanel.add(userLabel, BorderLayout.NORTH);

        // Панель с действиями (кнопками)
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new GridLayout(0, 1, 10, 10)); // Сетка для кнопок с вертикальными отступами
        actionsPanel.setBackground(Color.LIGHT_GRAY); // Цвет фона панели действий
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Отступы

        // Добавление кнопки для продуктов
        JButton productButton = new JButton("Products");
        productButton.setPreferredSize(new Dimension(200, 40)); // Фиксированный размер кнопки
        productButton.setFont(new Font("Arial", Font.BOLD, 14));
        productButton.addActionListener(e -> {
            // Открытие нового окна для продуктов
            ProductScreen.showProductScreen(username);
        });
        actionsPanel.add(productButton);

        // Добавление кнопки для заказов
        JButton orderButton = new JButton("Orders");
        orderButton.setPreferredSize(new Dimension(200, 40)); // Фиксированный размер кнопки
        orderButton.setFont(new Font("Arial", Font.BOLD, 14));
        orderButton.addActionListener(e -> {
            // Открытие нового окна для заказов
            OrderScreen.showOrderScreen(username);
        });
        actionsPanel.add(orderButton);

        // Кнопка для отображения списка сотрудников
        JButton employeeButton = new JButton("Employees");
        employeeButton.setPreferredSize(new Dimension(200, 40)); // Фиксированный размер кнопки
        employeeButton.setFont(new Font("Arial", Font.BOLD, 14));
        employeeButton.addActionListener(e -> {
            showEmployeesList();
        });
        actionsPanel.add(employeeButton);

        mainPanel.add(actionsPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static void showEmployeesList() {

        List<Employee> employees = new EmployeeDAO().getAllEmployees();
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Phone Number");
        tableModel.addColumn("Address");
        tableModel.addColumn("Start Date");

        for (Employee employee : employees) {
            Object[] rowData = {
                    employee.getId(),
                    employee.getFullName(),
                    employee.getPhoneNumber(),
                    employee.getAddress(),
                    employee.getHireDate()
            };
            tableModel.addRow(rowData);
        }

        JTable employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        employeeTable.setFillsViewportHeight(true);

        JFrame employeeFrame = new JFrame("Employee List");
        employeeFrame.setSize(600, 400);
        employeeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        employeeFrame.add(scrollPane, BorderLayout.CENTER);
        employeeFrame.setVisible(true);
    }
}
