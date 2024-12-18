package com.warehouse.ui.mainPages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.warehouse.dao.AccountDAO;
import com.warehouse.models.Account;
import org.mindrot.jbcrypt.BCrypt;

public class RegistrationPage {
    private JFrame frame;
    private JTextField loginField, fullNameField, phoneField, emailField;
    private JPasswordField passwordField, confirmPasswordField;

    public RegistrationPage() {
        frame = new JFrame("Регистрация");
        frame.setSize(400, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Панель для компонентов
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 10, 10));

        JLabel fullNameLabel = new JLabel("Полное имя:");
        JLabel phoneLabel = new JLabel("Номер телефона:");
        JLabel emailLabel = new JLabel("E-mail:");
        JLabel loginLabel = new JLabel("Логин:");
        JLabel passwordLabel = new JLabel("Пароль:");
        JLabel confirmPasswordLabel = new JLabel("Подтвердите пароль:");

        fullNameField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        loginField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        JButton registerButton = new JButton("Зарегистрироваться");
        JButton backButton = new JButton("Назад");

        panel.add(fullNameLabel);
        panel.add(fullNameField);
        panel.add(phoneLabel);
        panel.add(phoneField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(loginLabel);
        panel.add(loginField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(confirmPasswordLabel);
        panel.add(confirmPasswordField);
        panel.add(registerButton);
        panel.add(backButton);

        frame.add(panel);

        // Обработчик нажатия кнопки "Зарегистрироваться"
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fullName = fullNameField.getText();
                String phone = phoneField.getText();
                String email = emailField.getText();
                String login = loginField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (validateInput(fullName, phone, email, login, password, confirmPassword)) {
                    saveAccount(fullName, phone, email, login, password);
                }
            }
        });

        // Обработчик нажатия кнопки "Назад"
        backButton.addActionListener(e -> {
            frame.dispose();
            new LoginPage(); // Возврат к странице входа
        });

        frame.setVisible(true);
    }

    private boolean validateInput(String fullName, String phone, String email, String login, String password, String confirmPassword) {
        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || login.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Все поля должны быть заполнены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!phone.matches("\\+?\\d{10,15}")) {
            JOptionPane.showMessageDialog(frame, "Введите корректный номер телефона!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(frame, "Введите корректный адрес электронной почты!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(frame, "Пароли не совпадают!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        AccountDAO accountDAO = new AccountDAO();
        if (accountDAO.getAccountByLogin(login) != null) {
            JOptionPane.showMessageDialog(frame, "Логин уже используется!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (accountDAO.getAccountByPhone(phone) != null) {
            JOptionPane.showMessageDialog(frame, "Номер телефона уже используется!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (accountDAO.getAccountByEmail(email) != null) {
            JOptionPane.showMessageDialog(frame, "E-mail уже используется!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }


    private void saveAccount(String fullName, String phone, String email, String login, String password) {
        Account account = new Account();
        account.setFullName(fullName);
        account.setPhoneNumber(phone);
        account.setLogin(login);
        account.setPassword(BCrypt.hashpw(password, BCrypt.gensalt())); // Хешируем пароль

        AccountDAO accountDAO = new AccountDAO();
        if (accountDAO.createAccount(account)) {
            JOptionPane.showMessageDialog(frame, "Регистрация успешна!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            new LoginPage(); // Возврат на страницу входа
        } else {
            JOptionPane.showMessageDialog(frame, "Ошибка регистрации. Попробуйте снова.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
