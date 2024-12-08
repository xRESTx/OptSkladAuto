package com.warehouse.ui.dialog;

import com.warehouse.dao.DepartmentDAO;
import com.warehouse.models.Department;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditDepartmentDialog extends JDialog {

    private JTextField nameField;
    private JButton saveButton, cancelButton;
    private Department department;

    public EditDepartmentDialog(Frame parent, Department department) {
        super(parent, department == null ? "Add Department" : "Edit Department", true);
        this.department = department;

        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(3, 2));

        // Поля ввода
        add(new JLabel("Department Name:"));
        nameField = new JTextField(department != null ? department.getName() : "");
        add(nameField);

        // Кнопки
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        add(saveButton);
        add(cancelButton);

        // Слушатели событий
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDepartment();
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    private void saveDepartment() {
        String departmentName = nameField.getText().trim();

        if (departmentName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Department name cannot be empty.");
            return;
        }

        if (department == null) {
            // Новый департамент
            Department newDepartment = new Department();
            newDepartment.setName(departmentName);

            // Добавляем новый департамент в базу данных
            DepartmentDAO.addDepartment(newDepartment);
            JOptionPane.showMessageDialog(this, "Department added successfully.");
        } else {
            // Редактируем существующий департамент
            department.setName(departmentName);

            // Обновляем департамент в базе данных
            DepartmentDAO.update(department);
            JOptionPane.showMessageDialog(this, "Department updated successfully.");
        }

        dispose();
    }
}
