package com.warehouse.ui;

import javax.swing.*;

public class OrdersPage {

    private JFrame ordersFrame;

    public OrdersPage() {
        // Создание окна для отображения заказов
        ordersFrame = new JFrame("Страница заказов");
        ordersFrame.setSize(600, 400);
        ordersFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ordersFrame.setLocationRelativeTo(null); // Окно будет в центре экрана

        // Простой панель с текстом (вы можете заменить на реальный список заказов)
        JPanel ordersPanel = new JPanel();
        ordersPanel.add(new JLabel("Здесь будут отображаться все заказы"));

        // Добавление панели на окно
        ordersFrame.add(ordersPanel);
        ordersFrame.setVisible(true);
    }
}
