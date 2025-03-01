package com.warehouse.ui.dialog;

import com.warehouse.dao.SupplierDAO;
import com.warehouse.models.Supplier;

import javax.swing.*;
import java.awt.*;

public class SupplierForm extends JDialog {
    private JTextField nameField;
    private JTextField contactPersonField;
    private JTextField phoneNumberField;
    private JTextField emailField;
    private SupplierDAO supplierDAO;
    private Supplier supplier;

    public SupplierForm(JFrame parent, Supplier supplier) {
        super(parent, true);
        this.supplierDAO = new SupplierDAO();
        this.supplier = supplier;

        setTitle((supplier == null) ? "Добавить поставщика" : "Редактировать поставщика");
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Название:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Контактное лицо:"));
        contactPersonField = new JTextField();
        add(contactPersonField);

        add(new JLabel("Телефон:"));
        phoneNumberField = new JTextField();
        add(phoneNumberField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        add(saveButton);
        add(cancelButton);

        if (supplier != null) {
            nameField.setText(supplier.getName());
            contactPersonField.setText(supplier.getContactPerson());
            phoneNumberField.setText(supplier.getPhoneNumber());
            emailField.setText(supplier.getEmail());
        }

        saveButton.addActionListener(e -> saveSupplier());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void saveSupplier() {
        String name = nameField.getText();
        String contactPerson = contactPersonField.getText();
        String phoneNumber = phoneNumberField.getText();
        String email = emailField.getText();

        if (name.isEmpty() || contactPerson.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (supplier == null) {
            supplier = new Supplier();
        }

        supplier.setName(name);
        supplier.setContactPerson(contactPerson);
        supplier.setPhoneNumber(phoneNumber);
        supplier.setEmail(email);

        if (supplier.getSupplierId() == 0) {
            supplierDAO.saveSupplier(supplier);
        } else {
            supplierDAO.updateSupplier(supplier);
        }

        dispose();
    }
}
