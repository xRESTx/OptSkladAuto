package com.warehouse.ui;

import javax.swing.*;

public class LimitedFunctionalityPage {

    public LimitedFunctionalityPage() {
        JFrame frame = new JFrame("Ограниченный функционал");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Центр экрана

        JPanel panel = new JPanel();
        panel.add(new JLabel("Страница с ограниченным функционалом для клиентов"));

        // Кнопки только для добавления товаров в заказ и создания заказа
        JButton addProductButton = new JButton("Добавить товар в заказ");
        JButton createOrderButton = new JButton("Создать заказ");

        panel.add(addProductButton);
        panel.add(createOrderButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}
