package com.warehouse.ui.dialog;

import com.warehouse.dao.DepartmentDAO;
import com.warehouse.dao.EmployeeDAO;
import com.warehouse.models.Department;
import com.warehouse.models.Employee;

import javax.swing.*;
import java.awt.*;

public class EditEmployeeDialog extends JDialog {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField departmentField;
    private JButton saveButton;
    private String employeeLogin;

    public EditEmployeeDialog(Frame parent, String employeeLogin) {
        super(parent, "Edit Employee", true);
        this.employeeLogin = employeeLogin;

        setLayout(new GridLayout(4, 2));

        EmployeeDAO employeeDAO = new EmployeeDAO();
        Employee employee = employeeDAO.getEmployeeByLogin(employeeLogin);

        add(new JLabel("First Name:"));
        firstNameField = new JTextField(employee.getFirstName());
        add(firstNameField);

        add(new JLabel("Last Name:"));
        lastNameField = new JTextField(employee.getLastName());
        add(lastNameField);

        add(new JLabel("Department ID:"));
        departmentField = new JTextField(String.valueOf(employee.getDepartment().getDepartmentId()));
        add(departmentField);

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveEmployee());
        add(saveButton);

        setSize(300, 200);
        setLocationRelativeTo(parent);
    }

    private void saveEmployee() {
        try {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            int departmentId = Integer.parseInt(departmentField.getText());

            Department department = DepartmentDAO.findById(departmentId);
            if (department == null) {
                JOptionPane.showMessageDialog(this, "Invalid department ID!");
                return;
            }

            Employee updatedEmployee = new Employee();
            updatedEmployee.setEmployeeLogin(employeeLogin);
            updatedEmployee.setFirstName(firstName);
            updatedEmployee.setLastName(lastName);
            updatedEmployee.setDepartment(department);

            EmployeeDAO employeeDAO = new EmployeeDAO();
            employeeDAO.update(updatedEmployee);

            JOptionPane.showMessageDialog(this, "Employee updated successfully!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating employee: " + ex.getMessage());
        }
    }
}
