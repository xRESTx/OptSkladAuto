package com.warehouse.page.adminPage;

import com.warehouse.entities.Product;
import com.warehouse.entities.Department;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductsPage {

    public static void showProductsPage() {
        JFrame frame = new JFrame("Product Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Product List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных продуктов
        String[] columnNames = {"ID", "Department", "Name", "Price", "Stock Quantity", "Expiration Date", "Description"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable productTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(productTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления продукта
        JButton addButton = new JButton("Add Product");
        addButton.addActionListener(e -> addProduct(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования продукта
        JButton editButton = new JButton("Edit Product");
        editButton.addActionListener(e -> editProduct(productTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления продукта
        JButton deleteButton = new JButton("Delete Product");
        deleteButton.addActionListener(e -> deleteProduct(productTable, tableModel)); // Добавляем обработчик
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadProductData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void deleteProduct(JTable productTable, DefaultTableModel tableModel) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a product to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this product?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Product.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                // Получаем продукт по ID
                Product product = session.get(Product.class, productId);
                if (product != null) {
                    // Удаляем продукт из базы данных
                    session.delete(product);
                    session.getTransaction().commit();

                    // Удаляем строку из таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Product deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Product not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting product: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }


    private static void loadProductData(DefaultTableModel tableModel) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Product.class)
                .addAnnotatedClass(Department.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех продуктов из базы данных
            List<Product> products = session.createQuery("from Product", Product.class).list();

            // Добавление данных в таблицу
            for (Product product : products) {
                tableModel.addRow(new Object[]{
                        product.getId(),
                        product.getDepartment().getName(),
                        product.getName(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.getExpiryDate(),
                        product.getDescription()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            factory.close();
        }
    }

    private static void addProduct(DefaultTableModel tableModel) {
        // Создаем выпадающий список для департаментов
        JComboBox<Department> departmentComboBox = new JComboBox<>();

        // Загружаем существующие департаменты из базы данных
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Department.class)
                .addAnnotatedClass(Product.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            // Загружаем департаменты
            List<Department> departments = session.createQuery("from Department", Department.class).list();
            for (Department department : departments) {
                departmentComboBox.addItem(department);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading departments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Поля для других данных
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockQuantityField = new JTextField();
        JTextField expirationDateField = new JTextField();
        JTextField descriptionField = new JTextField();

        // Панель для ввода данных
        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Department:"));
        panel.add(departmentComboBox);
        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Stock Quantity:"));
        panel.add(stockQuantityField);
        panel.add(new JLabel("Expiration Date (Month):"));
        panel.add(expirationDateField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        // Диалоговое окно для ввода
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Department selectedDepartment = (Department) departmentComboBox.getSelectedItem();
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stockQuantity = Integer.parseInt(stockQuantityField.getText());
            Integer expirationDate = expirationDateField.getText().isEmpty() ? null : Integer.parseInt(expirationDateField.getText());
            String description = descriptionField.getText();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                // Создаем и сохраняем новый продукт
                Product product = new Product(name, selectedDepartment, price, expirationDate, description);
                product.setStockQuantity(stockQuantity);

                session.save(product);
                session.getTransaction().commit();

                // Обновляем таблицу
                tableModel.addRow(new Object[]{
                        product.getId(),
                        selectedDepartment.getName(),
                        product.getName(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.getExpiryDate(),
                        product.getDescription()
                });

                JOptionPane.showMessageDialog(null, "Product added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding product: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        factory.close();
    }

    private static void editProduct(JTable productTable, DefaultTableModel tableModel) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a product to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 2);
        double currentPrice = (double) tableModel.getValueAt(selectedRow, 3);
        int currentStockQuantity = (int) tableModel.getValueAt(selectedRow, 4);
        Integer currentExpirationDate = (Integer) tableModel.getValueAt(selectedRow, 5);
        String currentDescription = (String) tableModel.getValueAt(selectedRow, 6);

        JTextField nameField = new JTextField(currentName);
        JTextField priceField = new JTextField(String.valueOf(currentPrice));
        JTextField stockQuantityField = new JTextField(String.valueOf(currentStockQuantity));
        JTextField expirationDateField = new JTextField(currentExpirationDate != null ? String.valueOf(currentExpirationDate) : "");
        JTextField descriptionField = new JTextField(currentDescription);

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Stock Quantity:"));
        panel.add(stockQuantityField);
        panel.add(new JLabel("Expiration Date (Month):"));
        panel.add(expirationDateField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText();
            double newPrice = Double.parseDouble(priceField.getText());
            int newStockQuantity = Integer.parseInt(stockQuantityField.getText());
            Integer newExpirationDate = expirationDateField.getText().isEmpty() ? null : Integer.parseInt(expirationDateField.getText());
            String newDescription = descriptionField.getText();

            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Product.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Product product = session.get(Product.class, productId);
                if (product != null) {
                    product.setName(newName);
                    product.setPrice(newPrice);
                    product.setStockQuantity(newStockQuantity);
                    product.setExpiryDate(newExpirationDate);
                    product.setDescription(newDescription);
                    session.update(product);

                    session.getTransaction().commit();

                    tableModel.setValueAt(newName, selectedRow, 2);
                    tableModel.setValueAt(newPrice, selectedRow, 3);
                    tableModel.setValueAt(newStockQuantity, selectedRow, 4);
                    tableModel.setValueAt(newExpirationDate, selectedRow, 5);
                    tableModel.setValueAt(newDescription, selectedRow, 6);

                    JOptionPane.showMessageDialog(null, "Product updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Product not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating product: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            factory.close();
        }
    }
}
