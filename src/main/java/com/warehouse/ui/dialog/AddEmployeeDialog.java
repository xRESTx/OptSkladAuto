package com.warehouse.ui.dialog;

import com.warehouse.dao.ContractDAO;
import com.warehouse.dao.EmployeeDAO;
import com.warehouse.models.Contract;
import com.warehouse.models.Department;
import com.warehouse.models.Employee;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddEmployeeDialog extends JDialog {

    private JTextField passportDataField, loginField, lastNameField, firstNameField, middleNameField;
    private JTextField birthdayField, passwordField;
    private JComboBox<Department> departmentBox;

    public AddEmployeeDialog(Frame parent) {
        super(parent, "Add New Employee", true);
        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));

        // Поля формы
        formPanel.add(new JLabel("Passport Data:"));
        passportDataField = new JTextField();
        formPanel.add(passportDataField);

        formPanel.add(new JLabel("Login:"));
        loginField = new JTextField();
        formPanel.add(loginField);

        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Middle Name:"));
        middleNameField = new JTextField();
        formPanel.add(middleNameField);

        formPanel.add(new JLabel("Birthday (yyyy-MM-dd):"));
        birthdayField = new JTextField();
        formPanel.add(birthdayField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JTextField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Department:"));
        departmentBox = new JComboBox<>(EmployeeDAO.loadDepartments().toArray(new Department[0]));
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
        Employee newEmployee = new Employee();
        try {
            // Создаём нового сотрудника


            // Проверка на обязательные поля
            String passportDataText = passportDataField.getText().trim();
            if (passportDataText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Passport data is required.");
                return;
            }
            newEmployee.setPassportData(Integer.parseInt(passportDataText));

            newEmployee.setEmployeeLogin(loginField.getText().trim());
            if (newEmployee.getEmployeeLogin().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Login is required.");
                return;
            }

            newEmployee.setLastName(lastNameField.getText().trim());
            newEmployee.setFirstName(firstNameField.getText().trim());
            newEmployee.setMiddleName(middleNameField.getText().trim());

            // Парсим дату рождения
            String birthdayText = birthdayField.getText().trim();
            if (birthdayText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Birthday is required.");
                return;
            }
            Date birthday = new SimpleDateFormat("yyyy-MM-dd").parse(birthdayText);
            newEmployee.setBirthday(birthday);

            newEmployee.setPassword(passwordField.getText().trim());
            if (newEmployee.getPassword().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password is required.");
                return;
            }

            // Проверка на пустой департамент
            Department selectedDepartment = (Department) departmentBox.getSelectedItem();
            if (selectedDepartment == null) {
                JOptionPane.showMessageDialog(this, "Department selection is required.");
                return;
            }
            newEmployee.setDepartment(selectedDepartment);

            Contract newContract = new Contract();
            Date currentDate = new Date();
            newContract.setStartDate(currentDate);  // Устанавливаем текущую дату как начало контракта
            newContract.setEndDate(null);  // Конец контракта оставляем пустым
            newContract.setPosition(null);
            newContract.setSalary(1.0);
            // Сохраняем контракт
            ContractDAO.saveOrUpdate(newContract);
            newEmployee.setContract(newContract);  // Связываем контракт с сотрудником

            // Сохраняем сотрудника
            EmployeeDAO.saveOrUpdate(newEmployee);
            JOptionPane.showMessageDialog(this, "New employee and contract added successfully!");

//            int response = JOptionPane.showConfirmDialog(this,
//                    "Would you like to edit the new contract?",
//                    "Edit Contract",
//                    JOptionPane.YES_NO_OPTION);
//
//            if (response == JOptionPane.YES_OPTION) {
//                new EditContractDialog(this, newEmployee.getContract()).setVisible(true);
//            }
            dispose();


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee: " + ex.getMessage());
            ex.printStackTrace(); // Для диагностики
        }
    }


}
