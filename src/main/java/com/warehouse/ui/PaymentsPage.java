package com.warehouse.ui;

import javax.swing.*;

public class PaymentsPage {

    private JFrame paymentsFrame;

    public PaymentsPage() {
        // Создание окна для отображения платежей
        paymentsFrame = new JFrame("Страница платежей");
        paymentsFrame.setSize(600, 400);
        paymentsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        paymentsFrame.setLocationRelativeTo(null); // Окно будет в центре экрана

        // Простой панель с текстом (вы можете заменить на реальный список платежей)
        JPanel paymentsPanel = new JPanel();
        paymentsPanel.add(new JLabel("Здесь будут отображаться все платежи"));

        // Добавление панели на окно
        paymentsFrame.add(paymentsPanel);
        paymentsFrame.setVisible(true);
    }
}
