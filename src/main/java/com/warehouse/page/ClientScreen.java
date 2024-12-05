package com.warehouse.page;

import javax.swing.*;
import java.awt.*;

public class ClientScreen {

    public static void showClientScreen(String clientFullName,int clientID) {
        JFrame frame = new JFrame("Client Dashboard");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        JLabel clientLabel = new JLabel("Welcome, " + clientFullName + "!");
        clientLabel.setHorizontalAlignment(SwingConstants.CENTER);
        clientLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(clientLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton ordersButton = new JButton("Orders");
        ordersButton.addActionListener(e -> {
            OrderPage.showOrdersPage(clientFullName,clientID);
        });

        JButton requestsButton = new JButton("Appeals");
        requestsButton.addActionListener(e -> {
            RequestPage.showRequestPage(clientFullName);
        });

        JButton paymentsButton = new JButton("Payment");
        paymentsButton.addActionListener(e -> {
            PaymentPage.showPaymentPage(clientFullName,clientID);
        });

        buttonPanel.add(ordersButton);
        buttonPanel.add(requestsButton);
        buttonPanel.add(paymentsButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
