package com.warehouse.ui;

import javax.swing.*;

public class EmployeesPage {

    private JFrame employeesFrame;

    public EmployeesPage() {
        // Создание окна для отображения сотрудников
        employeesFrame = new JFrame("Страница сотрудников");
        employeesFrame.setSize(600, 400);
        employeesFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        employeesFrame.setLocationRelativeTo(null); // Окно будет в центре экрана

        // Простой панель с текстом (вы можете заменить на реальный список сотрудников)
        JPanel employeesPanel = new JPanel();
        employeesPanel.add(new JLabel("Здесь будут отображаться все сотрудники"));

        // Добавление панели на окно
        employeesFrame.add(employeesPanel);
        employeesFrame.setVisible(true);
    }
}
