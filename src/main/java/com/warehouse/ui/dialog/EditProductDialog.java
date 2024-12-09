package com.warehouse.ui.dialog;

import javax.swing.*;
import java.awt.*;
import com.warehouse.dao.AutotovarDAO;
import com.warehouse.models.Autotovar;

public class EditProductDialog extends JDialog {
    private JTextField nameField, costField, stockField, minimumField, descriptionField,categoryField;
    private JComboBox<String> categoryBox;
    private JCheckBox availableCheckBox;
    private int articul;

    public EditProductDialog(JFrame parent, int articul) {
        super(parent, "Edit Product", true);
        this.articul = articul;

        // Получаем данные продукта
        Autotovar product = AutotovarDAO.findById(articul);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Product not found!");
            dispose();
            return;
        }

        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(8, 2));

        // Поля редактирования
        add(new JLabel("Name:"));
        nameField = new JTextField(product.getName());
        add(nameField);

        add(new JLabel("Category:"));
        categoryField = new JTextField(product.getCategory());
        categoryField.setEditable(false);
        add(categoryField);

        add(new JLabel("Cost:"));
        costField = new JTextField(String.valueOf(product.getCost()));
        add(costField);

        add(new JLabel("Remaining stock:"));
        stockField = new JTextField(String.valueOf(product.getRemainingStock()));
        add(stockField);

        add(new JLabel("Minimum:"));
        minimumField = new JTextField(String.valueOf(product.getMinimum())); // Новое поле
        add(minimumField);

        add(new JLabel("Description:"));
        descriptionField = new JTextField(product.getDescription() == null ? "" : product.getDescription()); // Новое поле
        add(descriptionField);

        add(new JLabel("Available:"));
        availableCheckBox = new JCheckBox();
        availableCheckBox.setSelected(product.isAvailable());
        add(availableCheckBox);

        // Кнопки
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        add(saveButton);
        add(cancelButton);

        saveButton.addActionListener(e -> saveProduct());
        cancelButton.addActionListener(e -> dispose());
    }

    private void saveProduct() {
        try {
            String name = nameField.getText();
            String category = categoryBox.getSelectedItem().toString();
            double cost = Double.parseDouble(costField.getText());
            int stock = Integer.parseInt(stockField.getText());
            int minimum = Integer.parseInt(minimumField.getText()); // Читаем значение minimum
            String description = descriptionField.getText();
            boolean available = availableCheckBox.isSelected();

            // Вызов DAO для обновления данных
            Autotovar updatedProduct = new Autotovar();
            updatedProduct.setArticul(articul);
            updatedProduct.setName(name);
            updatedProduct.setCategory(category);
            updatedProduct.setCost(cost);
            updatedProduct.setRemainingStock(stock);
            updatedProduct.setMinimum(minimum);
            updatedProduct.setDescription(description);
            updatedProduct.setAvailable(available);

            AutotovarDAO.updateProduct(updatedProduct);

            JOptionPane.showMessageDialog(this, "Product updated successfully!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating product: " + e.getMessage());
        }
    }
}
