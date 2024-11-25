package com.warehouse.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.warehouse.dao.AccountDAO;
import com.warehouse.dao.EmployeeDAO;
import com.warehouse.models.Account;
import com.warehouse.models.Employee;

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
        JButton cancelButton = new JButton("Очистить");

        panel.add(loginLabel);
        panel.add(loginField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(cancelButton);

        frame.add(panel);

        // Обработчик нажатия кнопки "Войти"
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String password = new String(passwordField.getPassword());

                if (checkEmployeeCredentials(login, password)) {
                    // Если пользователь - сотрудник (Employee), открываем весь функционал
                    new AdminMainPage();
                    frame.dispose();// Главная страница для сотрудников
                } else if (checkAccountCredentials(login, password)) {
                    // Если пользователь - клиент (Account), открываем ограниченный функционал
                    new UserMainPage().setVisible(true);
                    frame.dispose();// Страница с ограниченным функционалом
                } else {
                    // Неверный логин/пароль
                    JOptionPane.showMessageDialog(frame, "Неверный логин или пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Обработчик нажатия кнопки "Отмена"
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginField.setText("");
                passwordField.setText("");
            }
        });

        frame.setVisible(true);
    }

    // Проверка учетных данных для сотрудника
    private boolean checkEmployeeCredentials(String login, String password) {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        Employee employee = employeeDAO.getEmployeeByLogin(login);

        // Проверяем, существует ли сотрудник и совпадает ли пароль
        if (employee != null && employee.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    // Проверка учетных данных для клиента
    private boolean checkAccountCredentials(String login, String password) {
        AccountDAO accountDAO = new AccountDAO();
        Account account = accountDAO.getAccountByLogin(login);

        // Проверяем, существует ли аккаунт и совпадает ли пароль
        if (account != null && account.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
