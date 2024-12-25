package com.warehouse.ui.workerPages;

import com.warehouse.ui.managerPages.*;
import com.warehouse.ui.adminPages.categoryPages.CategoryPage;

import javax.swing.*;
import java.awt.*;

public class WorkerMainPage {
    private JFrame frame;

    public WorkerMainPage() {
        // Создаем главное окно
        JFrame frame = new JFrame("Cтраница работников");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Центр экрана

        // Создаем основную панель
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Вертикальное расположение

        // Заголовок
        JLabel titleLabel = new JLabel("Главная страница для работников", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(20)); // Отступ

        // Кнопки перехода на страницы
        JButton ordersButton = new JButton("Просмотр заказов");
        JButton paymentsButton = new JButton("Просмотр платежей");
        JButton requestButton = new JButton("Просмотр запросов");
        JButton productsButton = new JButton("Просмотр товаров");

        // Центрируем кнопки
        ordersButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        productsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        paymentsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        requestButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Добавляем кнопки на панель
        panel.add(ordersButton);

        panel.add(Box.createVerticalStrut(10));
        panel.add(paymentsButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(requestButton);
        panel.add(Box.createVerticalStrut(10)); // Отступ между кнопками
        panel.add(productsButton);

        // Добавляем обработчики событий для кнопок
        ordersButton.addActionListener(e -> openOrdersPage(frame));
        productsButton.addActionListener(e -> openProductsPage(frame));
        paymentsButton.addActionListener(e -> openPaymentsPage(frame));
        requestButton.addActionListener(e -> openRequestPage(frame));

        // Добавляем панель на окно
        frame.add(panel);
        frame.setVisible(true);
    }

    private void openOrdersPage(JFrame parentFrame) {
        parentFrame.dispose(); // Закрываем главное окно
        new WorkerOrdersPage().setVisible(true); // Открываем страницу заказов
    }

    private void openRequestPage(JFrame parentFrame) {
        parentFrame.dispose(); // Закрываем главное окно
        new WorkersRequestsPage().setVisible(true); // Открываем страницу заказов
    }

    private void openProductsPage(JFrame parentFrame) {
        parentFrame.dispose();
        new WorkerProductPage().setVisible(true); // Открываем страницу товаров
    }

    private void openPaymentsPage(JFrame parentFrame) {
        parentFrame.dispose();
        new WorkersPaymentsPage().setVisible(true); // Открываем страницу платежей
    }

}
