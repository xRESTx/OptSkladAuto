package com.warehouse.ui;

import javax.swing.*;

public class ProductsPage {

    private JFrame productsFrame;

    public ProductsPage() {
        // Создание окна для отображения продуктов
        productsFrame = new JFrame("Страница товаров");
        productsFrame.setSize(600, 400);
        productsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        productsFrame.setLocationRelativeTo(null);

        JPanel productsPanel = new JPanel();
        productsPanel.add(new JLabel("Здесь будут отображаться все товары"));

        productsFrame.add(productsPanel);
        productsFrame.setVisible(true);
    }
}
