package com.warehouse.ui.dialog;

import com.warehouse.dao.AutotovarDAO;
import com.warehouse.models.Autotovar;
import com.warehouse.utils.HibernateUtil;
import org.hibernate.Session;

import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;

public class AddProductDialog extends JDialog {

    private JTextField nameField, descriptionField, costField, stockField, minimumField;
    private JComboBox<String> categoryBox;

    public AddProductDialog(JFrame parent) {
        super(parent, "Add Product", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        setLayout(new GridLayout(7, 2));
        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Description:"));
        descriptionField = new JTextField();
        add(descriptionField);

        add(new JLabel("Category:"));
        categoryBox = new JComboBox<>(new String[]{"Chemistry", "Electronics", "Wheels", "Accessories", "Lubricants", "Repair"});
        add(categoryBox);

        add(new JLabel("Cost:"));
        costField = new JTextField();
        add(costField);

        add(new JLabel("Stock:"));
        stockField = new JTextField();
        add(stockField);

        add(new JLabel("Minimum:"));
        minimumField = new JTextField();
        add(minimumField);


        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        add(saveButton);
        add(cancelButton);

        saveButton.addActionListener(e -> saveProduct());
        cancelButton.addActionListener(e -> dispose());
    }

    private void saveProduct() {
        String name = nameField.getText();
        String category = categoryBox.getSelectedItem().toString();
        double cost = 0;
        int stock = 0;
        int minimum = 0;

        // Валидация данных
        try {
            cost = Double.parseDouble(costField.getText());
            stock = Integer.parseInt(stockField.getText());
            minimum = Integer.parseInt(minimumField.getText());
            if (minimum <= 0) {
                JOptionPane.showMessageDialog(this, "Minimum must be greater than zero.");
                return;
            }
            if (cost <= 0) {
                JOptionPane.showMessageDialog(this, "Cost must be greater than zero.");
                return;
            }
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "Stock must be greater or equal than zero.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for cost, stock, and minimum.");
            return;
        }

        // Получаем максимальный articul из базы данных и увеличиваем его на 1
        int nextArticul = getNextArticul();

        // Создаем новый объект Autotovar
        Autotovar newProduct = new Autotovar();
        newProduct.setArticul(nextArticul);  // Устанавливаем сгенерированное значение articul
        newProduct.setName(name);
        newProduct.setCategory(category);
        newProduct.setCost(cost);
        newProduct.setRemainingStock(stock);
        newProduct.setMinimum(minimum);  // Устанавливаем значение для минимального количества
        newProduct.setAvailable(true);  // Продукт доступен по умолчанию

        // Вызов DAO для добавления нового продукта
        AutotovarDAO.addProduct(newProduct);

        // Уведомление и закрытие диалога
        JOptionPane.showMessageDialog(this, "Product added successfully!");
        dispose();
    }

    private int getNextArticul() {
        int maxArticul = 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Получаем максимальное значение articul из базы данных
            String hql = "SELECT MAX(a.articul) FROM Autotovar a";
            Query query = session.createQuery(hql);
            maxArticul = (Integer) ((org.hibernate.query.Query<?>) query).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return maxArticul + 1;  // Увеличиваем на 1
    }
}
