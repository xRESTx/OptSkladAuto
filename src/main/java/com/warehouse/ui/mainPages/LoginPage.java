package com.warehouse.ui.mainPages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.warehouse.ui.adminPages.AdminMainPage;
import com.warehouse.utils.*;


import org.mindrot.jbcrypt.BCrypt;

public class LoginPage {

    private JFrame frame;
    private JTextField loginField;
    private JPasswordField passwordField;
    private boolean visible;

    public LoginPage() {
        frame = new JFrame("Вход в систему");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Окно будет по центру экрана

        // Панель для компонентов
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel loginLabel = new JLabel("Логин:");
        JLabel passwordLabel = new JLabel("Пароль:");

        loginField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Войти");

        panel.add(loginLabel);
        panel.add(loginField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);

        frame.add(panel);

        // Обработчик нажатия кнопки "Войти"
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String password = new String(passwordField.getPassword());

//                if (checkEmployeeCredentials(login, password)) {
//                    // Сохраняем логин текущего пользователя
//                    SessionManager.setCurrentUserLogin(login);
//
//                    frame.dispose(); // Главная страница для сотрудников
//                } else if (checkAccountCredentials(login, password)) {
//                    // Сохраняем логин текущего пользователя
//                    SessionManager.setCurrentUserLogin(login);
//
//                    // Если пользователь - клиент (Account), открываем ограниченный функционал
//                    new UserMainPage().setVisible(true);
//                    frame.dispose(); // Страница с ограниченным функционалом
//                } else {
//                    // Неверный логин/пароль
//                    JOptionPane.showMessageDialog(frame, "Неверный логин или пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
//                }
            }

        });

        frame.setVisible(true);
    }

    private void navigateToEmployeePage(String department) {
        switch (department) {
            case "Admins":
                new AdminMainPage();
                break;
//            case "Managers":
//                new ManagerMainPage();
//                break;
//            default:
//                new WorkerMainPage();
//                break;
        }
        frame.dispose(); // Закрываем окно входа
    }


    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
