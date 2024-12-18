package com.warehouse.ui.mainPages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.warehouse.dao.AccountDAO;
import com.warehouse.dao.EmployeeDAO;
import com.warehouse.models.Account;
import com.warehouse.models.Employee;
import com.warehouse.ui.adminPages.AdminMainPage;
import com.warehouse.ui.managerPages.ManagerMainPage;
import com.warehouse.ui.userPages.UserMainPage;
import com.warehouse.ui.workerPages.WorkerMainPage;
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
        JButton registerButton = new JButton("Регистрация");
        panel.add(registerButton); // Добавляем кнопку на панель

// Обработчик нажатия кнопки "Регистрация"
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new RegistrationPage(); // Открываем страницу регистрации
            }
        });
        frame.add(panel);

        // Обработчик нажатия кнопки "Войти"
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String password = new String(passwordField.getPassword());

                if (checkEmployeeCredentials(login, password)) {
                    // Сохраняем логин текущего пользователя
                    SessionManager.setCurrentUserLogin(login);

                    frame.dispose(); // Главная страница для сотрудников
                } else if (checkAccountCredentials(login, password)) {
                    // Сохраняем логин текущего пользователя
                    SessionManager.setCurrentUserLogin(login);

                    // Если пользователь - клиент (Account), открываем ограниченный функционал
                    new UserMainPage().setVisible(true);
                    frame.dispose(); // Страница с ограниченным функционалом
                } else {
                    // Неверный логин/пароль
                    JOptionPane.showMessageDialog(frame, "Неверный логин или пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        frame.setVisible(true);
    }

    private boolean checkEmployeeCredentials(String login, String password) {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        Employee employee = employeeDAO.getEmployeeByLogin(login);

        // Проверяем, существует ли сотрудник и совпадает ли пароль (BCrypt)
        if (employee != null && BCrypt.checkpw(password, employee.getPassword())) {
            String department = employee.getDepartment().getName(); // Получаем название отдела
            navigateToEmployeePage(department); // Навигация на соответствующую страницу
            return true;
        }
        return false;
    }
    private void navigateToEmployeePage(String department) {
        switch (department) {
            case "Admins":
                new AdminMainPage();
                break;
            case "Managers":
                new ManagerMainPage();
                break;
            default:
                new WorkerMainPage();
                break;
        }
        frame.dispose(); // Закрываем окно входа
    }


    // Проверка учетных данных для клиента
    private boolean checkAccountCredentials(String login, String password) {
        AccountDAO accountDAO = new AccountDAO();
        Account account = accountDAO.getAccountByLogin(login);

        // Проверяем, существует ли аккаунт и совпадает ли пароль (BCrypt)
        if (account != null && BCrypt.checkpw(password, account.getPassword())) {
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
