package com.warehouse.ui.dialog;

import com.warehouse.models.OrderComponent;
import com.warehouse.models.Order;
import com.warehouse.dao.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OrderDetailsDialog extends JDialog {
    private JLabel orderIdLabel, loginLabel, dateOrderLabel, statusLabel, totalSumLabel;
    private JTable orderItemsTable;
    private JButton closeButton;

    public OrderDetailsDialog(JFrame parent, int orderId) {
        super(parent, "Order Details", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);

        // Создаем панели для отображения информации
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Загружаем данные о заказе
        Order order = OrderDAO.findOrderById(orderId);
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Order not found.");
            dispose();
            return;
        }

        // Инициализируем компоненты
        orderIdLabel = new JLabel("Order ID: " + order.getNumberOrder());
        loginLabel = new JLabel("Login: " + order.getAccount());
        dateOrderLabel = new JLabel("Order Date: " + order.getDateOrder().toString());
        statusLabel = new JLabel("Status: " + order.getStatus());
        totalSumLabel = new JLabel("Total Sum: " + order.getTotalSum());

        // Добавляем компоненты на панель
        panel.add(orderIdLabel);
        panel.add(loginLabel);
        panel.add(dateOrderLabel);
        panel.add(statusLabel);
        panel.add(totalSumLabel);

        // Создаем таблицу для отображения товаров в заказе
        List<OrderComponent> orderItems = OrderDAO.findOrderItems(orderId);
        String[] columnNames = {"Product", "Quantity", "Price", "Total"};
        Object[][] data = new Object[orderItems.size()][4];

        for (int i = 0; i < orderItems.size(); i++) {
            OrderComponent item = orderItems.get(i);
            data[i][0] = item.getAutotovar().getName();  // Получаем имя товара
            data[i][1] = item.getCount();
            data[i][2] = item.getSumma();
        }

        orderItemsTable = new JTable(data, columnNames);
        JScrollPane tableScroll = new JScrollPane(orderItemsTable);

        // Кнопка "Закрыть"
        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        // Добавляем таблицу и кнопку на панель
        panel.add(tableScroll);
        panel.add(closeButton);

        // Добавляем панель в диалоговое окно
        add(panel, BorderLayout.CENTER);
    }
}
