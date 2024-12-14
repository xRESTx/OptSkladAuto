package com.warehouse.page.adminPage;

import com.warehouse.entities.OrderItem;
import com.warehouse.entities.Order;
import com.warehouse.entities.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderItemsPage {

    public static void showOrderItemsPage() {
        JFrame frame = new JFrame("Order Item Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Order Item List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных элементов заказа
        String[] columnNames = {"ID", "Order ID", "Product Name", "Quantity"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable orderItemTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(orderItemTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления элемента заказа
        JButton addButton = new JButton("Add Order Item");
        addButton.addActionListener(e -> addOrderItem(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования элемента заказа
        JButton editButton = new JButton("Edit Order Item");
        editButton.addActionListener(e -> editOrderItem(orderItemTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления элемента заказа
        JButton deleteButton = new JButton("Delete Order Item");
        deleteButton.addActionListener(e -> deleteOrderItem(orderItemTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadOrderItemData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadOrderItemData(DefaultTableModel tableModel) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(OrderItem.class)
                .addAnnotatedClass(Order.class)
                .addAnnotatedClass(Product.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех элементов заказов из базы данных
            List<OrderItem> orderItems = session.createQuery("from OrderItem", OrderItem.class).list();

            // Добавление данных в таблицу
            for (OrderItem orderItem : orderItems) {
                tableModel.addRow(new Object[]{
                        orderItem.getId(),
                        orderItem.getOrder().getId(),
                        orderItem.getProduct().getName(),
                        orderItem.getQuantity()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading order items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            factory.close();
        }
    }

    private static void addOrderItem(DefaultTableModel tableModel) {
        // Создаем выпадающий список для заказов
        JComboBox<Order> orderComboBox = new JComboBox<>();

        // Создаем выпадающий список для продуктов
        JComboBox<Product> productComboBox = new JComboBox<>();

        // Загружаем существующие заказы и продукты из базы данных
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Order.class)
                .addAnnotatedClass(Product.class)
                .addAnnotatedClass(OrderItem.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            // Загружаем заказы
            List<Order> orders = session.createQuery("from Order", Order.class).list();
            for (Order order : orders) {
                orderComboBox.addItem(order);
            }

            // Загружаем продукты
            List<Product> products = session.createQuery("from Product", Product.class).list();
            for (Product product : products) {
                productComboBox.addItem(product);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading orders or products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Поле для количества
        JTextField quantityField = new JTextField();

        // Панель для ввода данных
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Order:"));
        panel.add(orderComboBox);
        panel.add(new JLabel("Product:"));
        panel.add(productComboBox);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);

        // Диалоговое окно для ввода
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Order Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Order selectedOrder = (Order) orderComboBox.getSelectedItem();
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            int quantity = Integer.parseInt(quantityField.getText());

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                // Создаем и сохраняем новый элемент заказа
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(selectedOrder);
                orderItem.setProduct(selectedProduct);
                orderItem.setQuantity(quantity);

                session.save(orderItem);
                session.getTransaction().commit();

                // Обновляем таблицу
                tableModel.addRow(new Object[]{
                        orderItem.getId(),
                        selectedOrder.getId(),
                        selectedProduct.getName(),
                        orderItem.getQuantity()
                });

                JOptionPane.showMessageDialog(null, "Order Item added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding order item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        factory.close();
    }

    private static void editOrderItem(JTable orderItemTable, DefaultTableModel tableModel) {
        int selectedRow = orderItemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an order item to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderItemId = (int) tableModel.getValueAt(selectedRow, 0);
        int currentOrderId = (int) tableModel.getValueAt(selectedRow, 1);
        String currentProductName = (String) tableModel.getValueAt(selectedRow, 2);
        int currentQuantity = (int) tableModel.getValueAt(selectedRow, 3);

        JTextField quantityField = new JTextField(String.valueOf(currentQuantity));

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Order Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int newQuantity = Integer.parseInt(quantityField.getText());

            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(OrderItem.class)
                    .addAnnotatedClass(Order.class)
                    .addAnnotatedClass(Product.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                OrderItem orderItem = session.get(OrderItem.class, orderItemId);
                if (orderItem != null) {
                    orderItem.setQuantity(newQuantity);
                    session.update(orderItem);

                    session.getTransaction().commit();

                    tableModel.setValueAt(newQuantity, selectedRow, 3);

                    JOptionPane.showMessageDialog(null, "Order Item updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Order Item not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating order item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            factory.close();
        }
    }
    private static void deleteOrderItem(JTable orderItemTable, DefaultTableModel tableModel) {
        int selectedRow = orderItemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an order item to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderItemId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirmation = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this order item?",
                "Delete Order Item", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(OrderItem.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                // Получение элемента заказа для удаления
                OrderItem orderItem = session.get(OrderItem.class, orderItemId);
                if (orderItem != null) {
                    session.delete(orderItem);
                    session.getTransaction().commit();

                    // Удаляем строку из таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Order Item deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Order Item not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting order item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            factory.close();
        }
    }
}
