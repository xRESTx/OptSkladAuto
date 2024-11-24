package com.warehouse.ui;

import javax.swing.*;

public class MainPage {

    public MainPage() {
        JFrame frame = new JFrame("Главная страница");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Центр экрана

        JPanel panel = new JPanel();
        panel.add(new JLabel("Главная страница для сотрудников"));

        // Дополнительные кнопки и функционал для сотрудников
        JButton ordersButton = new JButton("Просмотр заказов");
        JButton productsButton = new JButton("Просмотр товаров");

        panel.add(ordersButton);
        panel.add(productsButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}
