package com.warehouse.page;

import com.warehouse.page.clientPage.*;

import javax.swing.*;
import java.awt.*;

public class ClientDashboard {
    public ClientDashboard(int clientId) {
        JFrame clientFrame = new JFrame("Client Dashboard");
        clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientFrame.setSize(600, 400);
        clientFrame.setLocationRelativeTo(null);
        clientFrame.setLayout(new BorderLayout());

        // Заголовок
        JLabel titleLabel = new JLabel("Client Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        clientFrame.add(titleLabel, BorderLayout.NORTH);

        // Панель для кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 2, 10, 10));  // 3x2 grid to allow for 5 buttons
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Создание кнопок
        String[] buttonLabels = {"View Tariffs", "View Meters", "View Invoices", "View Payments"};
        Runnable[] actions = {
                () -> TariffsPage.showTariffsPage(),
                () -> MeterPage.showMeterPage(clientId),
//                () -> MeterReadingPage.showMeterReadingPage(clientId),
                () -> InvoicePage.showInvoicePage(clientId),
                () -> PaymentPage.showPaymentPage(clientId)
        };

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            int finalI = i;
            button.addActionListener(e -> actions[finalI].run());
            buttonPanel.add(button);
        }

        clientFrame.add(buttonPanel, BorderLayout.CENTER);

        // Кнопка выхода
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.addActionListener(e -> System.exit(0));

        JPanel exitPanel = new JPanel();
        exitPanel.add(exitButton);
        clientFrame.add(exitPanel, BorderLayout.SOUTH);

        clientFrame.setLocationRelativeTo(null);
        clientFrame.setVisible(true);
    }
}
