package com.warehouse.page;

import com.warehouse.dao.OrderDAO;
import com.warehouse.dao.OrderItemDAO;
import com.warehouse.dao.ProductDAO;
import com.warehouse.entities.Order;
import com.warehouse.entities.OrderItem;
import com.warehouse.entities.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderPage {

    private static Order currentOrder = null;

    public static void showOrdersPage(String clientFullName, int clientId) {

        JFrame frame = createFrame("Orders", 800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel label = new JLabel("Orders for " + clientFullName);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.NORTH);

        OrderDAO orderDAO = new OrderDAO();
        List<Order> orders = orderDAO.getOrdersByClientId(clientId);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Order ID", "Status", "Total Amount", "Order Date"}, 0);
        orders.forEach(order -> model.addRow(new Object[]{order.getId(), order.getStatus(), order.getAmount(), order.getOrderDate()}));

        JTable ordersTable = new JTable(model);
        frame.add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton createOrderButton = new JButton("Create Order");
        JButton viewOrderButton = new JButton("View Order");

        buttonPanel.add(createOrderButton);
        buttonPanel.add(viewOrderButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Обработчик для создания нового заказа
        createOrderButton.addActionListener(e -> {
            currentOrder = null; // Сбрасываем текущий заказ, чтобы создать новый
            showCreateOrderPage(clientId, model);
        });

        // Обработчик для просмотра и редактирования существующего заказа
        viewOrderButton.addActionListener(e -> {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow != -1) {
                int orderId = (int) model.getValueAt(selectedRow, 0);
                OrderDAO orderDAO1 = new OrderDAO();
                currentOrder = orderDAO1.findById(orderId); // Загружаем выбранный заказ
                if (currentOrder != null) {
                    showOrderDetails(currentOrder); // Открываем детали существующего заказа
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to load order.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an order to view.");
            }
        });

        frame.setVisible(true);
    }

    private static void showCreateOrderPage(int clientId, DefaultTableModel ordersTableModel) {
        JFrame frame = createFrame("Create Order", 600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel label = new JLabel("Create New Order");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.NORTH);

        // Таблица для выбора товаров
        DefaultTableModel productsModel = new DefaultTableModel(new String[]{"Product ID", "Product Name", "Stock Quantity"}, 0);
        JTable productsTable = new JTable(productsModel);
        frame.add(new JScrollPane(productsTable), BorderLayout.CENTER);

        // Получение списка товаров и их количество
        ProductDAO productDAO = new ProductDAO();
        List<Product> products = productDAO.findAll();
        for (Product product : products) {
            productsModel.addRow(new Object[]{product.getId(), product.getName(), product.getStockQuantity()});
        }

        // Панель для кнопок
        JPanel buttonPanel = new JPanel();
        JButton addItemButton = new JButton("Add Item");
        JButton closeButton = new JButton("Cancel");

        buttonPanel.add(addItemButton);
        buttonPanel.add(closeButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Список добавленных товаров
        DefaultTableModel orderItemsModel = new DefaultTableModel(new String[]{"Product ID", "Product Name", "Quantity"}, 0);
        JTable orderItemsTable = new JTable(orderItemsModel);
        frame.add(new JScrollPane(orderItemsTable), BorderLayout.EAST);

        // Создаем объект DAO для добавления товаров в заказ
        OrderItemDAO orderItemDAO = new OrderItemDAO();

        // Обработчик для добавления товара в заказ
        addItemButton.addActionListener(e -> {
            if (currentOrder == null) {
                // Создание нового заказа, если он еще не создан
                OrderDAO orderDAO = new OrderDAO();
                currentOrder = orderDAO.createOrder(clientId); // Создаем заказ только один раз
                if (currentOrder == null) {
                    JOptionPane.showMessageDialog(frame, "Failed to create order.");
                    return;
                }
            }

            int selectedRow = productsTable.getSelectedRow();
            if (selectedRow != -1) {
                int productId = (int) productsModel.getValueAt(selectedRow, 0);
                String quantityStr = JOptionPane.showInputDialog(frame, "Enter quantity (available: " + productsModel.getValueAt(selectedRow, 2) + "):");
                if (quantityStr != null) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);

                        // Получение продукта для проверки доступного количества
                        Product product = productDAO.findById(productId);
                        if (product != null && quantity <= product.getStockQuantity()) {
                            // Передаем заказ в метод для добавления элемента
                            OrderItem newItem = orderItemDAO.addItemToOrder(currentOrder, productId, quantity); // Передаем объект заказа

                            if (newItem != null) {
                                orderItemsModel.addRow(new Object[]{
                                        newItem.getProduct().getId(),
                                        newItem.getProduct().getName(),
                                        newItem.getQuantity()
                                });
                                JOptionPane.showMessageDialog(frame, "Item added to order!");
                            } else {
                                JOptionPane.showMessageDialog(frame, "Failed to add item to order.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Not enough stock available.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid quantity entered.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a product to add.");
            }
        });

        // Обработчик для закрытия окна
        closeButton.addActionListener(e -> {
            currentOrder = null;  // Сброс текущего заказа
            frame.dispose();      // Закрытие окна
        });

        frame.setVisible(true);
    }

    private static void showOrderDetails(Order order) {
        JFrame frame = new JFrame("Order Details");
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Изменено на DISPOSE_ON_CLOSE

        JLabel label = new JLabel("Order #" + order.getId() + " Details");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.NORTH);

        // Таблица с товарами заказа
        DefaultTableModel model = new DefaultTableModel(new String[]{"Product Name", "Quantity", "Price"}, 0);
        OrderItemDAO orderItemDAO = new OrderItemDAO();
        List<OrderItem> orderItems = orderItemDAO.findByOrderId(order.getId());
        for (OrderItem item : orderItems) {
            model.addRow(new Object[]{item.getProduct().getName(), item.getQuantity(), item.getProduct().getPrice()});
        }
        JTable orderItemsTable = new JTable(model);
        frame.add(new JScrollPane(orderItemsTable), BorderLayout.CENTER);

        // Панель для добавления новых товаров
        JPanel buttonPanel = new JPanel();
        JButton addItemButton = new JButton("Add Item");
        buttonPanel.add(addItemButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Обработчик для добавления товара в заказ
        addItemButton.addActionListener(e -> {
            showCreateOrderPage(order.getClient().getClientId(), model); // Позволяет добавить товары в текущий заказ
        });

        frame.setVisible(true);
    }

    private static JFrame createFrame(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Изменено на DO_NOTHING_ON_CLOSE
        frame.setLocationRelativeTo(null);
        return frame;
    }
}
