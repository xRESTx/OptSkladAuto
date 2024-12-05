package com.warehouse.page;

import com.warehouse.dao.DepartmentDAO;
import com.warehouse.dao.ProductDAO;
import com.warehouse.entities.Department;
import com.warehouse.entities.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.List;

public class ProductScreen {

    public static void showProductScreen(String username) {
        JFrame frame = new JFrame("Product Management");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel);

        JLabel label = new JLabel("Welcome, " + username + " (Employee)!");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        // Таблица для отображения продуктов
        JTable productTable = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{
                "ID", "Name", "Department", "Price", "Stock Quantity", "Expiry Date", "Description"
        }, 0);
        productTable.setModel(tableModel);
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        loadProductData(tableModel);

        JButton addButton = new JButton("Add Product");
        addButton.addActionListener(e -> addProduct(frame, tableModel));
        panel.add(addButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }


    private static void loadProductData(DefaultTableModel tableModel) {
        ProductDAO productDAO = new ProductDAO();
        List<Product> products = productDAO.findAll();

        if (products != null) {
            tableModel.setRowCount(0);
            for (Product product : products) {
                tableModel.addRow(new Object[]{
                        product.getId(),
                        product.getName(),
                        product.getDepartment().getName(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.getExpiryDate() + " months",
                        (product.getDescription() != null && !product.getDescription().isEmpty())
                                ? product.getDescription()
                                : "N/A"
                });
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error loading product data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private static void addProduct(JFrame parent, DefaultTableModel tableModel) {
        // Создание диалогового окна для ввода данных
        JDialog dialog = new JDialog(parent, "Add Product", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        // Основная панель с двумя колонками
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Левая колонка - метки
        JPanel labelPanel = new JPanel(new GridLayout(5, 1));
        labelPanel.add(new JLabel("Name:"));
        labelPanel.add(new JLabel("Department:"));
        labelPanel.add(new JLabel("Price:"));
        labelPanel.add(new JLabel("Expiration (Months):"));
        labelPanel.add(new JLabel("Description:"));

        // Правая колонка - ввод данных
        JPanel inputPanel = new JPanel(new GridLayout(5, 1));
        JTextField nameField = new JTextField();
        JComboBox<String> departmentComboBox = new JComboBox<>();
        JTextField priceField = new JTextField();
        JTextField expiryField = new JTextField(); // Длительность срока хранения в месяцах
        JTextField descriptionField = new JTextField();

        inputPanel.add(nameField);
        inputPanel.add(departmentComboBox);
        inputPanel.add(priceField);
        inputPanel.add(expiryField);
        inputPanel.add(descriptionField);

        // Загрузка данных отделов в JComboBox
        DepartmentDAO departmentDAO = new DepartmentDAO();
        List<Department> departments = departmentDAO.findAll();
        if (departments != null) {
            for (Department department : departments) {
                departmentComboBox.addItem(department.getId() + " - " + department.getName());
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Error loading departments.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mainPanel.add(labelPanel);
        mainPanel.add(inputPanel);

        // Кнопки управления
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                // Сбор данных
                String name = nameField.getText();
                String selectedDepartment = (String) departmentComboBox.getSelectedItem();
                if (selectedDepartment == null) {
                    throw new IllegalArgumentException("Department is not selected.");
                }

                int departmentId = Integer.parseInt(selectedDepartment.split(" - ")[0]);
                double price = Double.parseDouble(priceField.getText());
                int expiryDateMonths = Integer.parseInt(expiryField.getText());
                String description = descriptionField.getText();

                // Создание и сохранение продукта
                ProductDAO productDAO = new ProductDAO();
                Department department = departments.stream()
                        .filter(dep -> dep.getId() == departmentId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid department ID."));

                Product product = new Product(name, department, price, expiryDateMonths, description);

                productDAO.save(product);

                // Обновление таблицы
                loadProductData(tableModel);

                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Добавление панелей в диалоговое окно
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
