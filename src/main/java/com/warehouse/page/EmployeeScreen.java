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
        mainPanel.add(userLabel, BorderLayout.NORTH);

        // Панель с действиями (кнопками)
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));

        // Добавление кнопки для продуктов
        JPanel productActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel productLabel = new JLabel("Manage Products:");
        JButton productButton = new JButton("Products");

        productButton.setPreferredSize(new Dimension(100, 30));
        productButton.setMinimumSize(new Dimension(100, 30));
        productButton.setMaximumSize(new Dimension(100, 30));

        productButton.addActionListener(e -> {
            // Открытие нового окна для продуктов
            ProductScreen.showProductScreen(username);
        });

        productActionPanel.add(productLabel);
        productActionPanel.add(productButton);
        actionsPanel.add(productActionPanel);

        JPanel exampleActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel exampleLabel = new JLabel("Show orders:");
        JButton exampleButton = new JButton("Orders");

        exampleButton.setPreferredSize(new Dimension(100, 30));
        exampleButton.setMinimumSize(new Dimension(100, 30));
        exampleButton.setMaximumSize(new Dimension(100, 30));


        exampleButton.addActionListener(e -> {
            // Открытие нового окна для заказов
            OrderScreen.showOrderScreen(username);
        });


        exampleActionPanel.add(exampleLabel);
        exampleActionPanel.add(exampleButton);
        actionsPanel.add(exampleActionPanel);

        // Кнопка для отображения списка сотрудников
        JPanel employeeActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel employeeLabel = new JLabel("Show Employees:");
        JButton employeeButton = new JButton("Employees");

        employeeButton.setPreferredSize(new Dimension(100, 30));
        employeeButton.setMinimumSize(new Dimension(100, 30));
        employeeButton.setMaximumSize(new Dimension(100, 30));

        employeeButton.addActionListener(e -> {
            // Открытие нового окна для списка сотрудников
            showEmployeesList();
        });

        employeeActionPanel.add(employeeLabel);
        employeeActionPanel.add(employeeButton);
        actionsPanel.add(employeeActionPanel);

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
