package com.warehouse.ui.dialog;

import com.warehouse.dao.EmployeeDAO;
import com.warehouse.models.Contract;
import com.warehouse.models.Department;
import com.warehouse.models.Employee;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditEmployeeDialog extends JDialog {

    private JTextField passportDataField, loginField, lastNameField, firstNameField, middleNameField;
    private JTextField birthdayField, passwordField;
    private JComboBox<Contract> contractBox;
    private JComboBox<Department> departmentBox;

    private Employee employee;

    public EditEmployeeDialog(Frame parent, Employee employee) {
        super(parent, "Edit Employee", true);
        this.employee = employee;

        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));

        // Поля формы
        formPanel.add(new JLabel("Passport Data:"));
        passportDataField = new JTextField(String.valueOf(employee.getPassportData()));
        passportDataField.setEditable(false); // Паспортные данные нельзя редактировать
        formPanel.add(passportDataField);

        formPanel.add(new JLabel("Login:"));
        loginField = new JTextField(employee.getEmployeeLogin());
        formPanel.add(loginField);

        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField(employee.getLastName());
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField(employee.getFirstName());
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Middle Name:"));
        middleNameField = new JTextField(employee.getMiddleName());
        formPanel.add(middleNameField);

        formPanel.add(new JLabel("Birthday (yyyy-MM-dd):"));
        birthdayField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(employee.getBirthday()));
        formPanel.add(birthdayField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JTextField(employee.getPassword());
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Contract:"));
        contractBox = new JComboBox<>(EmployeeDAO.loadContracts().toArray(new Contract[0]));
        contractBox.setSelectedItem(employee.getContract());
        formPanel.add(contractBox);

        formPanel.add(new JLabel("Department:"));
        departmentBox = new JComboBox<>(EmployeeDAO.loadDepartments().toArray(new Department[0]));
        departmentBox.setSelectedItem(employee.getDepartment());
        formPanel.add(departmentBox);

        add(formPanel, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveEmployee());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveEmployee() {
        try {
            Employee newEmployee = new Employee();
            Contract newContract = new Contract();

            // Установка полей сотрудника
            newEmployee.setPassportData(Integer.parseInt(passportDataField.getText().trim()));
            newEmployee.setEmployeeLogin(loginField.getText().trim());
            newEmployee.setLastName(lastNameField.getText().trim());
            newEmployee.setFirstName(firstNameField.getText().trim());
            newEmployee.setMiddleName(middleNameField.getText().trim());
            newEmployee.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthdayField.getText().trim()));
            newEmployee.setPassword(passwordField.getText().trim());
            newEmployee.setDepartment((Department) departmentBox.getSelectedItem());

            // Настройка нового контракта
            newContract.setStartDate(new Date()); // Укажите дополнительные поля контракта

            // Сохранение сотрудника и контракта в базе
            EmployeeDAO.addEmployeeWithContract(newEmployee, newContract);

            JOptionPane.showMessageDialog(this, "Employee added successfully!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee: " + ex.getMessage());
        }
    }

}
