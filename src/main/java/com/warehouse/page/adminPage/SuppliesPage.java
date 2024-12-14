package com.warehouse.page.adminPage;

import com.toedter.calendar.JDateChooser;
import com.warehouse.entities.Supplier;
import com.warehouse.entities.Product;
import com.warehouse.entities.Delivery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class SuppliesPage {

    public static void showSuppliesPage() {
        JFrame frame = new JFrame("Supply Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Supply List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных поставок
        String[] columnNames = {"ID", "Supplier", "Product", "Supply Date", "Quantity", "Supply Cost"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable supplyTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(supplyTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления поставки
        JButton addButton = new JButton("Add Supply");
        addButton.addActionListener(e -> addSupply(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования поставки
        JButton editButton = new JButton("Edit Supply");
        editButton.addActionListener(e -> editSupply(supplyTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления поставки
        JButton deleteButton = new JButton("Delete Supply");
        deleteButton.addActionListener(e -> deleteSupply(supplyTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadSupplyData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadSupplyData(DefaultTableModel tableModel) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Delivery.class)
                .addAnnotatedClass(Supplier.class)
                .addAnnotatedClass(Product.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех поставок из базы данных
            List<Delivery> supplies = session.createQuery("from Delivery", Delivery.class).list();

            // Добавление данных в таблицу
            for (Delivery supply : supplies) {
                tableModel.addRow(new Object[]{
                        supply.getId(),
                        supply.getSupplier().getName(),
                        supply.getProduct().getName(),
                        supply.getDeliveryDate(),
                        supply.getQuantity(),
                        supply.getDeliveryCost()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading supplies: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            factory.close();
        }
    }

    private static void deleteSupply(JTable supplyTable, DefaultTableModel tableModel) {
        int selectedRow = supplyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a supply to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int supplyId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this supply?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Delivery.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Delivery supply = session.get(Delivery.class, supplyId);
                if (supply != null) {
                    session.delete(supply);
                    session.getTransaction().commit();

                    // Удаляем строку из таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Supply deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Supply not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting supply: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }

    private static void addSupply(DefaultTableModel tableModel) {
        // Создаем выпадающие списки для поставщиков и продуктов
        JComboBox<Supplier> supplierComboBox = new JComboBox<>();
        JComboBox<Product> productComboBox = new JComboBox<>();

        // Загружаем поставщиков и продукты из базы данных
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Supplier.class)
                .addAnnotatedClass(Product.class)
                .addAnnotatedClass(Delivery.class) // Важно добавить сущность для Delivery
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            // Загружаем поставщиков
            List<Supplier> suppliers = session.createQuery("from Supplier", Supplier.class).list();
            for (Supplier supplier : suppliers) {
                supplierComboBox.addItem(supplier);
            }

            // Загружаем продукты
            List<Product> products = session.createQuery("from Product", Product.class).list();
            for (Product product : products) {
                productComboBox.addItem(product);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading suppliers or products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Поля для других данных
        JTextField quantityField = new JTextField();
        JTextField costField = new JTextField();

        // Создаем JDateChooser для выбора даты поставки
        JDateChooser supplyDateChooser = new JDateChooser();
        supplyDateChooser.setDateFormatString("yyyy-MM-dd"); // Формат отображаемой даты

        // Панель для ввода данных
        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Supplier:"));
        panel.add(supplierComboBox);
        panel.add(new JLabel("Product:"));
        panel.add(productComboBox);
        panel.add(new JLabel("Supply Date:"));
        panel.add(supplyDateChooser); // Добавляем JDateChooser в панель
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Supply Cost:"));
        panel.add(costField);

        // Диалоговое окно для ввода
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Supply", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Supplier selectedSupplier = (Supplier) supplierComboBox.getSelectedItem();
            Product selectedProduct = (Product) productComboBox.getSelectedItem();

            // Проверка на null для выбранных значений
            if (selectedSupplier == null || selectedProduct == null) {
                JOptionPane.showMessageDialog(null, "Please select a supplier and a product.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Получаем выбранную дату из JDateChooser
            java.util.Date utilDate = supplyDateChooser.getDate();
            if (utilDate != null) {
                LocalDate supplyDate = LocalDate.ofInstant(utilDate.toInstant(), java.time.ZoneId.systemDefault());
                int quantity = Integer.parseInt(quantityField.getText());
                double cost = Double.parseDouble(costField.getText());

                try (Session session = factory.openSession()) {
                    session.beginTransaction();

                    // Создаем и сохраняем новую поставку
                    Delivery supply = new Delivery();
                    supply.setSupplier(selectedSupplier);
                    supply.setProduct(selectedProduct);
                    supply.setDeliveryDate(supplyDate);
                    supply.setQuantity(quantity);
                    supply.setDeliveryCost(cost);

                    session.save(supply);
                    session.getTransaction().commit();

                    // Обновляем таблицу
                    tableModel.addRow(new Object[]{
                            supply.getId(),
                            selectedSupplier.getName(),
                            selectedProduct.getName(),
                            supply.getDeliveryDate(),
                            supply.getQuantity(),
                            supply.getDeliveryCost()
                    });

                    JOptionPane.showMessageDialog(null, "Supply added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding supply: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a supply date.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        factory.close();
    }

    private static void editSupply(JTable supplyTable, DefaultTableModel tableModel) {
        int selectedRow = supplyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a supply to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int supplyId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentSupplier = (String) tableModel.getValueAt(selectedRow, 1);
        String currentProduct = (String) tableModel.getValueAt(selectedRow, 2);
        LocalDate currentDate = (LocalDate) tableModel.getValueAt(selectedRow, 3);
        int currentQuantity = (int) tableModel.getValueAt(selectedRow, 4);
        double currentCost = (double) tableModel.getValueAt(selectedRow, 5);

        // Создаем диалог для редактирования
        JTextField quantityField = new JTextField(String.valueOf(currentQuantity));
        JTextField costField = new JTextField(String.valueOf(currentCost));
        JTextField supplyDateField = new JTextField(currentDate.toString());

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Supply Date (YYYY-MM-DD):"));
        panel.add(supplyDateField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Supply Cost:"));
        panel.add(costField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Supply", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            LocalDate newSupplyDate = LocalDate.parse(supplyDateField.getText());
            int newQuantity = Integer.parseInt(quantityField.getText());
            double newCost = Double.parseDouble(costField.getText());

            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Delivery.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Delivery supply = session.get(Delivery.class, supplyId);
                if (supply != null) {
                    supply.setDeliveryDate(newSupplyDate);
                    supply.setQuantity(newQuantity);
                    supply.setDeliveryCost(newCost);

                    session.update(supply);
                    session.getTransaction().commit();

                    // Обновляем строку в таблице
                    tableModel.setValueAt(newSupplyDate, selectedRow, 3);
                    tableModel.setValueAt(newQuantity, selectedRow, 4);
                    tableModel.setValueAt(newCost, selectedRow, 5);

                    JOptionPane.showMessageDialog(null, "Supply updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Supply not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating supply: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }
}
