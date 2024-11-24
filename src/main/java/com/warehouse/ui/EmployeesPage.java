package com.warehouse.ui;

import javax.swing.*;
import java.awt.*;
import com.warehouse.dao.*;
import com.warehouse.ui.dialog.AddEmployeeDialog;
import com.warehouse.ui.dialog.EditEmployeeDialog;

class EmployeesPage extends JFrame {
    private JTable employeesTable;
    private JButton backButton, addButton, editButton, deleteButton, filterButton;
    private JTextField searchField;

    public EmployeesPage() {
        setTitle("Employees Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Панель инструментов
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        filterButton = new JButton("Filter/Search");
        addButton = new JButton("Add Employee");
        editButton = new JButton("Edit Employee");
        deleteButton = new JButton("Delete Employee");
        backButton = new JButton("Назад");
        backButton.addActionListener(e -> {
            employeesTable.disable(); // Закрыть текущую страницу
            new MainPage(); // Вернуться на главную
        });

        toolbar.add(backButton, BorderLayout.SOUTH);
        toolbar.add(new JLabel("Search:"));
        toolbar.add(searchField);
        toolbar.add(filterButton);
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);

        // Таблица сотрудников
        String[] columnNames = {"Login", "Full Name", "Department", "Contract", "Birthday"};
        Object[][] data = {}; // Здесь будет загрузка данных из базы
        employeesTable = new JTable(data, columnNames);

        JScrollPane tableScroll = new JScrollPane(employeesTable);

        add(toolbar, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        // Слушатели действий
        addButton.addActionListener(e -> new AddEmployeeDialog(this).setVisible(true));
        editButton.addActionListener(e -> editEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        filterButton.addActionListener(e -> filterEmployees());

        
    }

    private void editEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow >= 0) {
            String login = employeesTable.getValueAt(selectedRow, 0).toString();
            new EditEmployeeDialog(this, login).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit.");
        }
    }

    private void deleteEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this employee?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                String login = employeesTable.getValueAt(selectedRow, 0).toString();
                // Вызов DAO для удаления
                // employeeDAO.deleteEmployeeByLogin(login);
                JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
        }
    }

    private void filterEmployees() {
        String searchTerm = searchField.getText();
        // Вызов DAO для получения отфильтрованных данных
        EmployeeDAO.filterEmployees(searchTerm);
    }
}
