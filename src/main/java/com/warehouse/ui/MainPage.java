package com.warehouse.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPage {

    public MainPage() {
        // Создаем главное окно
        JFrame frame = new JFrame("Главная страница");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Центр экрана

        // Создаем основную панель
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Вертикальное расположение

        // Заголовок
        JLabel titleLabel = new JLabel("Главная страница для сотрудников", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(20)); // Отступ

        // Кнопки перехода на страницы
        JButton ordersButton = new JButton("Управление заказами");
        JButton productsButton = new JButton("Просмотр товаров");
        JButton employeesButton = new JButton("Управление сотрудниками");
        JButton paymentsButton = new JButton("Управление платежами");

        // Центрируем кнопки
        ordersButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        productsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        employeesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        paymentsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Добавляем кнопки на панель
        panel.add(ordersButton);
        panel.add(Box.createVerticalStrut(10)); // Отступ между кнопками
        panel.add(productsButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(employeesButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(paymentsButton);

        // Добавляем обработчики событий для кнопок
        ordersButton.addActionListener(e -> openOrdersPage(frame));
        productsButton.addActionListener(e -> openProductsPage(frame));
        employeesButton.addActionListener(e -> openEmployeesPage(frame));
        paymentsButton.addActionListener(e -> openPaymentsPage(frame));

        // Добавляем панель на окно
        frame.add(panel);
        frame.setVisible(true);
    }

    private void openOrdersPage(JFrame parentFrame) {
        parentFrame.dispose(); // Закрываем главное окно
        new OrdersPage().setVisible(true); // Открываем страницу заказов
    }

    private void openProductsPage(JFrame parentFrame) {
        parentFrame.dispose();
        new ProductsPage().setVisible(true); // Открываем страницу товаров
    }

    private void openEmployeesPage(JFrame parentFrame) {
        parentFrame.dispose();
        new EmployeesPage().setVisible(true); // Открываем страницу сотрудников
    }

    private void openPaymentsPage(JFrame parentFrame) {
        parentFrame.dispose();
        new PaymentsPage(); // Открываем страницу платежей
    }
}
