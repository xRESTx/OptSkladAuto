package com.warehouse.ui.adminPages;

import javax.swing.*;
import java.awt.*;

public class AdminMainPage {

    public AdminMainPage() {
        // Создаем главное окно
        JFrame frame = new JFrame("Главная страница управления");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Создаем основную панель
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Заголовок
        JLabel titleLabel = new JLabel("Система управления автозаправками", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        // Кнопки перехода на страницы
        JButton stationsButton = new JButton("Управление заправками");
        JButton fuelTypesButton = new JButton("Управление видами топлива");
        JButton suppliersButton = new JButton("Управление поставщиками");
        JButton employeesButton = new JButton("Управление сотрудниками");
        JButton fuelStockButton = new JButton("Управление запасами топлива");
        JButton fuelSupplyButton = new JButton("Управление поставками топлива");
        JButton transactionsButton = new JButton("Просмотр транзакций");

        // Центрируем кнопки
        stationsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        fuelTypesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        suppliersButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        employeesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        fuelStockButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        fuelSupplyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        transactionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Добавляем кнопки на панель
        panel.add(stationsButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(fuelTypesButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(suppliersButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(employeesButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(fuelStockButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(fuelSupplyButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(transactionsButton);

        // Добавляем обработчики событий для кнопок
        stationsButton.addActionListener(e -> openStationsPage(frame));
        fuelTypesButton.addActionListener(e -> openFuelTypesPage(frame));
        suppliersButton.addActionListener(e -> openSuppliersPage(frame));
        employeesButton.addActionListener(e -> openEmployeesPage(frame));
        fuelStockButton.addActionListener(e -> openFuelStockPage(frame));
        fuelSupplyButton.addActionListener(e -> openFuelSupplyPage(frame));
        transactionsButton.addActionListener(e -> openTransactionsPage(frame));

        // Добавляем панель на окно
        frame.add(panel);
        frame.setVisible(true);
    }

    private void openStationsPage(JFrame parentFrame) {
        parentFrame.dispose();
        new StationsPage().setVisible(true);
    }

    private void openFuelTypesPage(JFrame parentFrame) {
        parentFrame.dispose();
        new FuelTypesPage().setVisible(true);
    }

    private void openSuppliersPage(JFrame parentFrame) {
        parentFrame.dispose();
        new SuppliersPage().setVisible(true);
    }

    private void openEmployeesPage(JFrame parentFrame) {
        parentFrame.dispose();
        new EmployeesPage().setVisible(true);
    }

    private void openFuelStockPage(JFrame parentFrame) {
        parentFrame.dispose();
        new FuelStockPage().setVisible(true);
    }

    private void openFuelSupplyPage(JFrame parentFrame) {
        parentFrame.dispose();
        new FuelSupplyPage().setVisible(true);
    }

    private void openTransactionsPage(JFrame parentFrame) {
        parentFrame.dispose();
        new TransactionsPage().setVisible(true);
    }
}
