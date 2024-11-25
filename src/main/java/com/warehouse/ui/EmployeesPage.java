package com.warehouse.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

import com.warehouse.dao.EmployeeDAO;
import com.warehouse.models.Employee;
import com.warehouse.ui.dialog.AddEmployeeDialog;
import com.warehouse.ui.dialog.EditContractDialog;
import com.warehouse.ui.dialog.EditEmployeeDialog;

public class EmployeesPage extends JFrame {
    private JTable employeesTable;
    private JButton backButton, addButton, editButton, deleteButton, filterButton;
    private JTextField searchField;

    public EmployeesPage() {
        setTitle("Employee Management");
        setSize(1400, 800);
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

        // Кнопка "Назад"
        backButton.addActionListener(e -> {
            dispose(); // Закрываем текущее окно
            new MainPage(); // Возвращаемся на главную страницу
        });

        toolbar.add(backButton);
        toolbar.add(new JLabel("Search:"));
        toolbar.add(searchField);
        toolbar.add(filterButton);
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);

        // Таблица сотрудников
        String[] columnNames = {"Passport Data", "Login", "Last Name", "First Name", "Middle Name", "Birthday", "Contract", "Department"};
        Object[][] data = {}; // Заглушка, данные будут загружаться из базы
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        employeesTable = new JTable(model);
        employeesTable.setFillsViewportHeight(true);
        employeesTable.setRowHeight(50);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        employeesTable.setRowSorter(sorter);

        JScrollPane tableScroll = new JScrollPane(employeesTable);

        // Добавляем компоненты в окно
        add(toolbar, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        // Слушатели действий
        addButton.addActionListener(e -> {
            new AddEmployeeDialog(this).setVisible(true);
            loadEmployees();
        });

        editButton.addActionListener(e -> {
            editEmployee();
        });

        deleteButton.addActionListener(e -> {
            deleteEmployee();
        });

        filterButton.addActionListener(e -> {
            filterEmployees();
        });

        loadEmployees(); // Загружаем данные сотрудников при инициализации
    }

    private void editEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int passportData = Integer.parseInt(employeesTable.getValueAt(selectedRow, 0).toString());
            Employee employee = EmployeeDAO.findById(passportData);

            // Открываем диалоговое окно для редактирования сотрудника
            new EditEmployeeDialog(this, employee).setVisible(true);
            loadEmployees();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit.");
        }
    }

    private void deleteEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Получаем паспортные данные выбранного сотрудника
            int passportData = Integer.parseInt(employeesTable.getValueAt(selectedRow, 0).toString());

            // Подтверждаем удаление
            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this employee?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    EmployeeDAO.deleteEmployee(passportData);
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                    loadEmployees(); // Перезагружаем список сотрудников
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error occurred while deleting employee: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
        }
    }

    private void filterEmployees() {
        String searchTerm = searchField.getText().trim(); // Убираем лишние пробелы
        if (searchTerm.isEmpty()) {
            loadEmployees();
        } else {
            List<Employee> filteredEmployees = EmployeeDAO.filterEmployees(searchTerm);
            updateEmployeeTable(filteredEmployees);
        }
    }

    private void loadEmployees() {
        List<Employee> allEmployees = EmployeeDAO.findAll();
        updateEmployeeTable(allEmployees);
    }

    private void updateEmployeeTable(List<Employee> employees) {
        String[] columnNames = {"Passport Data", "Login", "Last Name", "First Name", "Middle Name", "Birthday", "Contract position", "Department"};
        Object[][] data = new Object[employees.size()][8];
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            data[i][0] = emp.getPassportData();
            data[i][1] = emp.getEmployeeLogin();
            data[i][2] = emp.getLastName();
            data[i][3] = emp.getFirstName();
            data[i][4] = emp.getMiddleName();
            data[i][5] = emp.getBirthday();
            data[i][6] = emp.getContract().getPosition();
            data[i][7] = emp.getDepartment() != null ? emp.getDepartment().getName() : "N/A";
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        employeesTable.setModel(model);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        employeesTable.setRowSorter(sorter);
    }
}
