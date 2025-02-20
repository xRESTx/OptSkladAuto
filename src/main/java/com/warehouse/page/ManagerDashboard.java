package com.warehouse.page;

import com.warehouse.page.adminPage.*;

import javax.swing.*;
import java.awt.*;

public class ManagerDashboard {
    public static void showStartPage() {
        JFrame frame = new JFrame("Warehouse Management System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Заголовок
        JLabel titleLabel = new JLabel("Warehouse Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Панель для кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Создание кнопок
        String[] buttonLabels = {"Clients", "Tariffs", "Services", "Meters", "Meter Readings", "Invoices", "Payments"};
        Runnable[] actions = {
                ClientsPage::showClientsPage, TariffsPage::showTariffsPage, ServicesPage::showServicesPage,
                MeterPage::showMeterPage, MeterReadingPage::showMeterReadingPage,
                InvoicePage::showInvoicePage, PaymentPage::showPaymentPage
        };

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton("Go to " + buttonLabels[i] + " Page");
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            int finalI = i;
            button.addActionListener(e -> actions[finalI].run());
            buttonPanel.add(button);
        }

        frame.add(buttonPanel, BorderLayout.CENTER);

        // Кнопка выхода
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.addActionListener(e -> System.exit(0));

        JPanel exitPanel = new JPanel();
        exitPanel.add(exitButton);
        frame.add(exitPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
