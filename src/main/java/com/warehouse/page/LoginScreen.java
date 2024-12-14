package com.warehouse.page;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

import com.warehouse.dao.*;
import com.warehouse.entities.*;
import java.util.List;
import com.warehouse.dao.PositionDAO;
import com.warehouse.entities.Position;
import com.toedter.calendar.JDateChooser;

public class LoginScreen {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Login");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            JPanel panel = new JPanel();
            frame.add(panel);
            placeComponents(panel, frame);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void placeComponents(JPanel panel, JFrame frame) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 50, 165, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 120, 25);
        panel.add(loginButton);

        // Обработчик нажатия кнопки "Login"
        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());

            AccountDAO accountDAO = new AccountDAO();
            Account account = accountDAO.findByUsername(username); // Поиск по имени пользователя

            if (account != null && password.equals(account.getPassword())) { // Убедиться, что пароли совпадают
                JOptionPane.showMessageDialog(frame, "Login successful!");
                frame.dispose(); // Закрываем окно входа

                // Проверяем роль и открываем соответствующий экран
                switch (account.getRole().toLowerCase()) {
                    case "employee":
                        SwingUtilities.invokeLater(() -> EmployeeScreen.showEmployeeScreen(username));
                        break;
                    case "client":
                        int clientID = ClientDAO.getClientIdByUsername(username);
                        SwingUtilities.invokeLater(() -> ClientScreen.showClientScreen(username, clientID));
                        break;
                    case "admin":
                        SwingUtilities.invokeLater(() -> {
                            AdminScreen.showAdminScreen(username);
                        });
                        break;
                    case "manager":
                        SwingUtilities.invokeLater(() -> {
                            ManagerScreen.showManagerScreen(username);
                        });
                        break;
                    default:
                        JOptionPane.showMessageDialog(frame, "Unknown role: " + account.getRole());
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.");
            }
        });


        JButton registerButton = new JButton("Register");
        registerButton.setBounds(150, 80, 120, 25);
        panel.add(registerButton);

        // Обработчик нажатия кнопки "Register"
        registerButton.addActionListener(e -> {
            // Переход к экрану регистрации
            showRegisterDialog(panel, frame); // Передаем панель и фрейм
        });
    }
    private static boolean validateLogin(String username, String password) {
        AccountDAO accountDAO = new AccountDAO();
        Account account = accountDAO.findByUsername(username);
        if (account != null && account.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    private static void showRegisterDialog(JPanel parentPanel, JFrame frame) {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new GridLayout(0, 2));

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField1 = new JPasswordField(20);
        JPasswordField passwordField2 = new JPasswordField(20);
        String[] userTypes = {"Client", "Employee"};
        JComboBox<String> typeComboBox = new JComboBox<>(userTypes);

        registerPanel.add(new JLabel("Username:"));
        registerPanel.add(usernameField);
        registerPanel.add(new JLabel("Password:"));
        registerPanel.add(passwordField1);
        registerPanel.add(new JLabel("Confirm Password:"));
        registerPanel.add(passwordField2);
        registerPanel.add(new JLabel("Select Account Type:"));
        registerPanel.add(typeComboBox);

        int option = JOptionPane.showConfirmDialog(parentPanel, registerPanel, "Register", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password1 = new String(passwordField1.getPassword());
            String password2 = new String(passwordField2.getPassword());
            String selectedType = (String) typeComboBox.getSelectedItem();

            if (!password1.equals(password2)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Проверяем, существует ли пользователь
            if (validateLogin(username, password1)) {
                JOptionPane.showMessageDialog(null, "User already exists.");
                return;
            }

            // Показываем форму данных в зависимости от типа пользователя
            if ("Client".equals(selectedType)) {
                showClientDataForm(username, password1);
            } else {
                showEmployeeDataForm(username, password1);
            }

            // Окно НЕ закрывается после регистрации
            JOptionPane.showMessageDialog(null, "Registration completed successfully!");
        }
    }

    private static void showClientDataForm(String username, String password) {
        JPanel clientPanel = new JPanel(new GridLayout(0, 2));

        JTextField fullNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextArea addressField = new JTextArea();

        clientPanel.add(new JLabel("Full Name:"));
        clientPanel.add(fullNameField);
        clientPanel.add(new JLabel("Phone Number:"));
        clientPanel.add(phoneField);
        clientPanel.add(new JLabel("Email:"));
        clientPanel.add(emailField);
        clientPanel.add(new JLabel("Address:"));
        clientPanel.add(new JScrollPane(addressField));

        int option = JOptionPane.showConfirmDialog(null, clientPanel, "Client Information", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            // Создаем объект Account для клиента
            Account account = new Account(username, password, "client", LocalDate.now());

            // Сохраняем Account в базе данных (если необходимо)
            AccountDAO accountDAO = new AccountDAO();
            boolean accountSaved = accountDAO.save(account);

            if (accountSaved) {
                // Если аккаунт успешно сохранен, создаем и сохраняем клиента
                Client client = new Client(fullNameField.getText(), phoneField.getText(), emailField.getText(), addressField.getText(), account);
                ClientDAO clientDAO = new ClientDAO();
                boolean clientSaved = clientDAO.save(client);

                if (clientSaved) {
                    JOptionPane.showMessageDialog(null, "Client registration successful!");
                } else {
                    JOptionPane.showMessageDialog(null, "Error saving client data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Error saving account data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private static void showEmployeeDataForm(String username, String password) {
        JPanel employeePanel = new JPanel(new GridLayout(0, 2));

        JTextField fullNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextArea addressField = new JTextArea();
        JComboBox<Position> positionComboBox = new JComboBox<>();
        JDateChooser startDateChooser = new JDateChooser();

        // Загружаем позиции из базы данных
        PositionDAO positionDAO = new PositionDAO();
        List<Position> positions = positionDAO.findAll();
        positionComboBox.setModel(new DefaultComboBoxModel<>(positions.toArray(new Position[0])));

        employeePanel.add(new JLabel("Full Name:"));
        employeePanel.add(fullNameField);
        employeePanel.add(new JLabel("Phone Number:"));
        employeePanel.add(phoneField);
        employeePanel.add(new JLabel("Address:"));
        employeePanel.add(new JScrollPane(addressField));
        employeePanel.add(new JLabel("Position:"));
        employeePanel.add(positionComboBox);
        employeePanel.add(new JLabel("Start Date:"));
        employeePanel.add(startDateChooser);

        int option = JOptionPane.showConfirmDialog(null, employeePanel, "Employee Information", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            // Сохраняем Account сначала
            Account newAccount = new Account(username, password, "employee", LocalDate.now());
            AccountDAO accountDAO = new AccountDAO();
            accountDAO.save(newAccount);

            // Далее создаем Employee, используя только start_date
            Position selectedPosition = (Position) positionComboBox.getSelectedItem();
            LocalDate startDate = startDateChooser.getDate() != null ? new java.sql.Date(startDateChooser.getDate().getTime()).toLocalDate() : null;

            Employee employee = new Employee(fullNameField.getText(), phoneField.getText(), addressField.getText(), startDate, selectedPosition, newAccount);
            EmployeeDAO employeeDAO = new EmployeeDAO();
            employeeDAO.save(employee);
            JOptionPane.showMessageDialog(null, "Employee registration successful!");
        }
    }
}
