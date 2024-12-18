package com.warehouse.ui.managerPages;

import javax.swing.*;

public class ManagerMainPage {
    private JFrame frame;

    public ManagerMainPage() {
        frame = new JFrame("Менеджерская страница");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel label = new JLabel("Добро пожаловать, Менеджер!", SwingConstants.CENTER);
        frame.add(label);

        frame.setVisible(true);
    }
}
