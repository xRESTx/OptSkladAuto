package com.warehouse.ui.mainPages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.warehouse.dao.EmployeeDAO;
import com.warehouse.models.Employee;
import com.warehouse.ui.adminPages.AdminMainPage;
import com.warehouse.ui.managerPages.ManagerMainPage;
import com.warehouse.ui.workerPages.WorkerMainPage;
import com.warehouse.utils.SessionManager;

public class LoginPage {
    private JFrame frame;
    private JTextField loginField;
    private JPasswordField passwordField;
    private EmployeeDAO employeeDAO;

    public LoginPage() {
        employeeDAO = new EmployeeDAO();

        frame = new JFrame("Вход в систему");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Окно будет по центру экрана

        // Панель для компонентов
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel loginLabel = new JLabel("ФИО:");
        JLabel passwordLabel = new JLabel("Пароль:");

        loginField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Войти");

        panel.add(loginLabel);
        panel.add(loginField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Пустой JLabel для выравнивания
        panel.add(loginButton);

        frame.add(panel);

        // Обработчик нажатия кнопки "Войти"
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });

        frame.setVisible(true);
    }

    private void authenticateUser() {
        String fullName = loginField.getText().trim();
        String password = new String(passwordField.getPassword());

        Employee employee = employeeDAO.getEmployeeByFullNameAndPassword(fullName, password);

        if (employee != null) {
            SessionManager.setCurrentUserLogin(fullName);
            navigateToEmployeePage(employee.getPosition());
        } else {
            JOptionPane.showMessageDialog(frame, "Неверное ФИО или пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void navigateToEmployeePage(String department) {
        switch (department) {
            case "Admin":
                new AdminMainPage();
                break;
            case "Manager":
                new ManagerMainPage();
                break;
            default:
                new WorkerMainPage();
                break;
        }
        frame.dispose(); // Закрываем окно входа
    }
}
