package com.warehouse.ui.workerPages;

import com.warehouse.ui.mainPages.LoginPage;

import javax.swing.*;
import java.awt.*;

public class WorkerMainPage {

    public WorkerMainPage() {
        // Создаем главное окно
        JFrame frame = new JFrame("Страница работников");
        frame.setSize(800, 600);
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
        JButton fuelTypesButton = new JButton("Виды топлива");
        JButton fuelStockButton = new JButton("Остатки топлива");
        JButton transactionsButton = new JButton("Просмотр транзакций");
        JButton logoutButton = new JButton("Выйти из аккаунта");

        // Центрируем кнопки
        fuelTypesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        fuelStockButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        transactionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Добавляем кнопки на панель
        panel.add(fuelTypesButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(fuelStockButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(transactionsButton);
        panel.add(Box.createVerticalStrut(50));
        panel.add(logoutButton);

        // Добавляем обработчики событий для кнопок
        fuelTypesButton.addActionListener(e -> openFuelTypesPage(frame));
        fuelStockButton.addActionListener(e -> openFuelStockPage(frame));
        transactionsButton.addActionListener(e -> openTransactionsPage(frame));
        logoutButton.addActionListener(e -> logout(frame));

        // Добавляем панель на окно
        frame.add(panel);
        frame.setVisible(true);
    }

    private void openFuelTypesPage(JFrame parentFrame) {
        parentFrame.dispose();
        new FuelTypesPage().setVisible(true);
    }

    private void openFuelStockPage(JFrame parentFrame) {
        parentFrame.dispose();
        new FuelStockPage().setVisible(true);
    }

    private void openTransactionsPage(JFrame parentFrame) {
        parentFrame.dispose();
        new TransactionsPage().setVisible(true);
    }

    private void logout(JFrame parentFrame) {
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "Вы уверены, что хотите выйти?", "Выход", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            parentFrame.dispose();
            new LoginPage();
        }
    }
}
