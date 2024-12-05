package com.warehouse.page;

import com.warehouse.dao.OrderDAO;
import com.warehouse.dao.OrderItemDAO;
import com.warehouse.entities.Order;
import com.warehouse.entities.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderScreen {

    public static void showOrderScreen(String username) {
        JFrame frame = new JFrame("Orders Screen");
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel);

        JLabel label = new JLabel("Orders for: " + username);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        // Таблица для отображения заказов
        JTable orderTable = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{
                "Order ID", "Client Name", "Status", "Total Amount", "Order Date"
        }, 0);
        orderTable.setModel(tableModel);
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        // Кнопка для отображения состава заказа
        JButton viewItemsButton = new JButton("The composition of the order");
        viewItemsButton.addActionListener(e -> showOrderItems(orderTable));

        // Устанавливаем фиксированный размер кнопки
        viewItemsButton.setPreferredSize(new Dimension(200, 50));

        // Добавляем кнопку в нижнюю часть окна
        JPanel buttonPanel = new JPanel(); // Панель для размещения кнопки
        buttonPanel.add(viewItemsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных заказов
        loadOrderData(tableModel);

        frame.setVisible(true);
    }

    private static void loadOrderData(DefaultTableModel tableModel) {
        OrderDAO orderDAO = new OrderDAO();
        List<Order> orders = orderDAO.findAll();

        if (orders != null) {
            tableModel.setRowCount(0); // Очистка таблицы перед загрузкой данных
            for (Order order : orders) {
                tableModel.addRow(new Object[]{
                        order.getId(),
                        order.getClient() != null ? order.getClient().getFullName() : "Unknown Client",
                        order.getStatus(),
                        order.getAmount(),
                        order.getOrderDate()
                });
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error loading order data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void showOrderItems(JTable orderTable) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an order.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) orderTable.getValueAt(selectedRow, 0); // Получение ID заказа

        // Загрузка состава заказа
        OrderItemDAO orderItemDAO = new OrderItemDAO();
        List<OrderItem> orderItems = orderItemDAO.findByOrderId(orderId);

        // Отображение данных состава заказа в новом окне
        JDialog dialog = new JDialog();
        dialog.setTitle("The composition of the order #" + orderId);
        dialog.setSize(400, 300);

        JTable itemTable = new JTable();
        DefaultTableModel itemTableModel = new DefaultTableModel(new String[]{
                "Item ID", "Product ID", "Quantity"
        }, 0);
        itemTable.setModel(itemTableModel);

        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                itemTableModel.addRow(new Object[]{
                        item.getId(),
                        item.getProduct().getId(),
                        item.getQuantity()
                });
            }
        }

        dialog.add(new JScrollPane(itemTable));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
