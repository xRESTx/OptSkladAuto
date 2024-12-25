package com.warehouse.ui.managerPages;

import com.warehouse.ui.managerPages.*;
import com.warehouse.ui.adminPages.categoryPages.CategoryPage;

import javax.swing.*;
import java.awt.*;

public class ManagerMainPage {
    private JFrame frame;

    public ManagerMainPage() {
        // Создаем главное окно
        JFrame frame = new JFrame("Менеджерская страница");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Центр экрана

        // Создаем основную панель
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Вертикальное расположение

        // Заголовок
        JLabel titleLabel = new JLabel("Главная страница для менеджеров", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(20)); // Отступ

        // Кнопки перехода на страницы
        JButton ordersButton = new JButton("Управление заказами");
        JButton employeesButton = new JButton("Управление сотрудниками");
        JButton contractButton = new JButton("Управление контрактами");
        JButton paymentsButton = new JButton("Управление платежами");
        JButton requestButton = new JButton("Управление запросами");
        JButton departmentButton = new JButton("Просмотр отделов");
        JButton productsButton = new JButton("Просмотр товаров");

        // Центрируем кнопки
        ordersButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        productsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        employeesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contractButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        paymentsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        departmentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        requestButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Добавляем кнопки на панель
        panel.add(ordersButton);

        panel.add(Box.createVerticalStrut(10));
        panel.add(employeesButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(contractButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(paymentsButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(requestButton);
        panel.add(Box.createVerticalStrut(10)); // Отступ между кнопками
        panel.add(productsButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(departmentButton);

        // Добавляем обработчики событий для кнопок
        ordersButton.addActionListener(e -> openOrdersPage(frame));
        productsButton.addActionListener(e -> openProductsPage(frame));
        employeesButton.addActionListener(e -> openEmployeesPage(frame));
        contractButton.addActionListener(e -> openContractPage(frame));
        paymentsButton.addActionListener(e -> openPaymentsPage(frame));
        departmentButton.addActionListener(e -> openDepartmentPage(frame));
        requestButton.addActionListener(e -> openRequestPage(frame));

        // Добавляем панель на окно
        frame.add(panel);
        frame.setVisible(true);
    }

    private void openOrdersPage(JFrame parentFrame) {
        parentFrame.dispose(); // Закрываем главное окно
        new OrdersPage().setVisible(true); // Открываем страницу заказов
    }

    private void openRequestPage(JFrame parentFrame) {
        parentFrame.dispose(); // Закрываем главное окно
        new RequestsPage().setVisible(true); // Открываем страницу заказов
    }

    private void openProductsPage(JFrame parentFrame) {
        parentFrame.dispose();
        new ManagerProductPage().setVisible(true); // Открываем страницу товаров
    }

    private void openEmployeesPage(JFrame parentFrame) {
        parentFrame.dispose();
        new EmployeesPage().setVisible(true); // Открываем страницу сотрудников
    }

    private void openContractPage(JFrame parentFrame) {
        parentFrame.dispose();
        new ContractPage().setVisible(true); // Открываем страницу сотрудников
    }

    private void openPaymentsPage(JFrame parentFrame) {
        parentFrame.dispose();
        new PaymentsPage().setVisible(true); // Открываем страницу платежей
    }

    private void openDepartmentPage(JFrame parentFrame) {
        parentFrame.dispose();
        new DepartmentPage().setVisible(true); // Открываем страницу платежей
    }

}
