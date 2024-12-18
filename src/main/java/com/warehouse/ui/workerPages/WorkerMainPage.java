package com.warehouse.ui.workerPages;

import javax.swing.*;

public class WorkerMainPage {
    private JFrame frame;

    public WorkerMainPage() {
        frame = new JFrame("Страница работника");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel label = new JLabel("Добро пожаловать, Работник!", SwingConstants.CENTER);
        frame.add(label);

        frame.setVisible(true);
    }
}
