package com.warehouse.page;

import com.warehouse.dao.OrderDAO;
import com.warehouse.dao.OrderItemDAO;
import com.warehouse.entities.Order;
import com.warehouse.entities.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderPage {

    public static void showOrdersPage(String clientFullName, int clientId) {
        JFrame frame = createFrame("Orders", 800, 600);

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

        createOrderButton.addActionListener(e -> showCreateOrderPage(clientId, model));
        viewOrderButton.addActionListener(e -> viewOrderDetails(ordersTable, model));

        frame.setVisible(true);
    }

    private static void showCreateOrderPage(int clientId, DefaultTableModel ordersTableModel) {
        JFrame frame = createFrame("Create Order", 600, 400);

        JLabel label = new JLabel("Create New Order");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Product ID", "Quantity"}, 0);
        JTable itemsTable = new JTable(model);
        frame.add(new JScrollPane(itemsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addItemButton = new JButton("Add Item");
        JButton saveOrderButton = new JButton("Save Order");

        buttonPanel.add(addItemButton);
        buttonPanel.add(saveOrderButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        addItemButton.addActionListener(e -> addItemToTable(frame, model));
        saveOrderButton.addActionListener(e -> saveOrder(frame, clientId, model, ordersTableModel));

        frame.setVisible(true);
    }

    private static void showOrderDetailsPage(int orderId) {
        JFrame frame = createFrame("Order Details", 600, 400);

        JLabel label = new JLabel("Order Details for Order ID: " + orderId);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.NORTH);

        OrderItemDAO orderItemDAO = new OrderItemDAO();
        List<OrderItem> items = orderItemDAO.findByOrderId(orderId);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Product ID", "Quantity"}, 0);
        items.forEach(item -> model.addRow(new Object[]{item.getProduct().getId(), item.getQuantity()}));

        frame.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void addItemToTable(JFrame frame, DefaultTableModel model) {
        try {
            String productId = JOptionPane.showInputDialog(frame, "Enter Product ID:");
            String quantity = JOptionPane.showInputDialog(frame, "Enter Quantity:");
            if (productId != null && quantity != null) {
                model.addRow(new Object[]{Integer.parseInt(productId), Integer.parseInt(quantity)});
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input!");
        }
    }

    private static void saveOrder(JFrame frame, int clientId, DefaultTableModel itemsModel, DefaultTableModel ordersTableModel) {
        OrderDAO orderDAO = new OrderDAO();
        OrderItemDAO orderItemDAO = new OrderItemDAO();

        try {
            Order newOrder = orderDAO.createOrder(clientId);
            if (newOrder == null) throw new RuntimeException("Failed to create order.");

            double totalAmount = 0.0;
            for (int i = 0; i < itemsModel.getRowCount(); i++) {
                int productId = (int) itemsModel.getValueAt(i, 0);
                int quantity = (int) itemsModel.getValueAt(i, 1);
                OrderItem orderItem = orderItemDAO.addItemToOrder(newOrder, productId, quantity);
                totalAmount += calculateItemTotal(productId, quantity);
            }

            newOrder.setAmount(totalAmount);
            orderDAO.update(newOrder);

            ordersTableModel.addRow(new Object[]{newOrder.getId(), newOrder.getStatus(), totalAmount, newOrder.getOrderDate()});
            JOptionPane.showMessageDialog(frame, "Order created successfully!");
            frame.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error saving order: " + ex.getMessage());
        }
    }

    private static JFrame createFrame(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private static double calculateItemTotal(int productId, int quantity) {
        // TODO: Add actual price calculation logic
        return 10.0 * quantity; // Placeholder
    }

    private static void viewOrderDetails(JTable ordersTable, DefaultTableModel model) {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow != -1) {
            int orderId = (int) model.getValueAt(selectedRow, 0);
            showOrderDetailsPage(orderId);
        } else {
            JOptionPane.showMessageDialog(null, "Please select an order to view.");
        }
    }
}
