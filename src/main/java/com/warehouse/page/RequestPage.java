package com.warehouse.page;

import javax.swing.*;
import java.awt.*;

public class RequestPage {

    public static void showRequestPage(String clientFullName) {
        JFrame frame = new JFrame("Client Requests");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Requests from " + clientFullName);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.NORTH);

        // Пример содержимого
        JTextArea requestsArea = new JTextArea("The list of requests will be here...");
        requestsArea.setEditable(false);
        frame.add(new JScrollPane(requestsArea), BorderLayout.CENTER);

        frame.setLocationRelativeTo(null); // Центрирование на экране
        frame.setVisible(true);
    }
}
