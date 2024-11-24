package com.warehouse.ui.dialog;

import com.warehouse.dao.*;
import com.warehouse.models.*;

import javax.swing.*;
import java.awt.*;

public class AddEmployeeDialog extends JDialog {
    private JTextField firstNameField, lastNameField, departmentField, loginField;
    private JButton saveButton, cancelButton;

    public AddEmployeeDialog(JFrame parent) {
        super(parent, "Add Employee", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        setLayout(new GridLayout(5, 2));

        add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        add(firstNameField);

        add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        add(lastNameField);

        add(new JLabel("Department ID:"));
        departmentField = new JTextField();
        add(departmentField);

        add(new JLabel("Login:"));
        loginField = new JTextField();
        add(loginField);

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        add(saveButton);
        add(cancelButton);

        saveButton.addActionListener(e -> saveEmployee());
        cancelButton.addActionListener(e -> dispose());
    }

    private void saveEmployee() {
        try {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            int departmentId = Integer.parseInt(departmentField.getText());
            String login = loginField.getText();

            Department department = DepartmentDAO.findById(departmentId);
            if (department == null) {
                JOptionPane.showMessageDialog(this, "Invalid department ID!");
                return;
            }

            Employee newEmployee = new Employee();
            newEmployee.setFirstName(firstName);
            newEmployee.setLastName(lastName);
            newEmployee.setDepartment(department);
            newEmployee.setEmployeeLogin(login);

            EmployeeDAO.save(newEmployee);

            JOptionPane.showMessageDialog(this, "Employee added successfully!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee: " + ex.getMessage());
        }
    }

}
