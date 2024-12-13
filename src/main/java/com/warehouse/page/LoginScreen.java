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
    private static void showLoginDialog(JFrame frame) {
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(null, panel, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Проверка логина
            if (validateLogin(username, password)) {
                JOptionPane.showMessageDialog(null, "Login Successful!");

                // После успешного логина закрываем текущее окно
                frame.dispose();

                // Проверяем, является ли пользователь сотрудником или клиентом
                AccountDAO accountDAO = new AccountDAO();
                Account account = accountDAO.findByUsername(username);

                if (account != null) {
                    if (account.getRole().equals("employee")) {
                        EmployeeScreen.showEmployeeScreen(username);
                    } else if (account.getRole().equals("client")) {
                        int clientID = ClientDAO.getClientIdByUsername(username);
                        ClientScreen.showClientScreen(username,clientID);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password.");
            }
        }
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

    private static boolean registerUser() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));

        JTextField usernameField = new JTextField();
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);

        JPasswordField passwordField1 = new JPasswordField();
        JPasswordField passwordField2 = new JPasswordField();
        panel.add(new JLabel("Password:"));
        panel.add(passwordField1);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(passwordField2);

        JButton clientButton = new JButton("Client");
        JButton employeeButton = new JButton("Employee");
        panel.add(clientButton);
        panel.add(employeeButton);

        JPanel additionalInfoPanel = new JPanel();
        additionalInfoPanel.setLayout(new GridLayout(0, 2));

        JTextField fullNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextArea addressField = new JTextArea();
        additionalInfoPanel.add(new JLabel("Full Name:"));
        additionalInfoPanel.add(fullNameField);
        additionalInfoPanel.add(new JLabel("Phone:"));
        additionalInfoPanel.add(phoneField);
        additionalInfoPanel.add(new JLabel("Email:"));
        additionalInfoPanel.add(emailField);
        additionalInfoPanel.add(new JLabel("Address:"));
        additionalInfoPanel.add(new JScrollPane(addressField));

        JTextField employeeFullNameField = new JTextField();
        JTextField employeePhoneField = new JTextField();
        JTextArea employeeAddressField = new JTextArea();
        JComboBox<Position> positionComboBox = new JComboBox<>();
        JDateChooser startDateChooser = new JDateChooser();  // Заменили на startDateChooser
        additionalInfoPanel.add(new JLabel("Employee Full Name:"));
        additionalInfoPanel.add(employeeFullNameField);
        additionalInfoPanel.add(new JLabel("Employee Phone:"));
        additionalInfoPanel.add(employeePhoneField);
        additionalInfoPanel.add(new JLabel("Employee Address:"));
        additionalInfoPanel.add(new JScrollPane(employeeAddressField));
        additionalInfoPanel.add(new JLabel("Position:"));
        additionalInfoPanel.add(positionComboBox);
        additionalInfoPanel.add(new JLabel("Start Date:"));
        additionalInfoPanel.add(startDateChooser);

        additionalInfoPanel.setVisible(false);
        panel.add(additionalInfoPanel);

        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                additionalInfoPanel.setVisible(true);
                employeeFullNameField.setText("");
                employeePhoneField.setText("");
                employeeAddressField.setText("");
                positionComboBox.setVisible(false);
                startDateChooser.setVisible(false);
            }
        });

        employeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                additionalInfoPanel.setVisible(true);
                positionComboBox.setVisible(true);
                startDateChooser.setVisible(true);

                PositionDAO positionDAO = new PositionDAO();
                List<Position> positions = positionDAO.findAll();
                positionComboBox.setModel(new DefaultComboBoxModel<>(positions.toArray(new Position[0])));

                fullNameField.setText("");
                phoneField.setText("");
                emailField.setText("");
                addressField.setText("");
            }
        });

        int option = JOptionPane.showConfirmDialog(null, panel, "User Registration", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password1 = new String(passwordField1.getPassword());
            String password2 = new String(passwordField2.getPassword());

            if (!password1.equals(password2)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            String selectedType = clientButton.getModel().isPressed() ? "Client" : "Employee";

            AccountDAO accountDAO = new AccountDAO();
            Account newAccount = new Account(username, password1, selectedType.toLowerCase(), LocalDate.now());
            accountDAO.save(newAccount);

            if ("Client".equals(selectedType)) {
                Client client = new Client(fullNameField.getText(), phoneField.getText(), emailField.getText(), addressField.getText(), newAccount);
                ClientDAO clientDAO = new ClientDAO();
                clientDAO.save(client);
            } else if ("Employee".equals(selectedType)) {
                Position selectedPosition = (Position) positionComboBox.getSelectedItem();
                LocalDate startDate = startDateChooser.getDate() != null ? new java.sql.Date(startDateChooser.getDate().getTime()).toLocalDate() : null;

                Employee employee = new Employee(employeeFullNameField.getText(), employeePhoneField.getText(), employeeAddressField.getText(), startDate, selectedPosition, newAccount);
                EmployeeDAO employeeDAO = new EmployeeDAO();
                employeeDAO.save(employee);
            }
        }
        return true;
    }
}
