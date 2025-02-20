package com.warehouse.page;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.warehouse.entities.Client;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class LoginWindow {
    private JFrame frame;
    private JTextField clientIdField;
    private JPasswordField passwordField;

    public static void main(String[] args) {
        // Создаем и показываем окно входа
        SwingUtilities.invokeLater(() -> {
            LoginWindow window = new LoginWindow();
            window.frame.setVisible(true);
        });
    }

    public LoginWindow() {
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null); // Окно по центру экрана

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel clientIdLabel = new JLabel("Client ID:");
        JLabel passwordLabel = new JLabel("Password:");

        clientIdField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginActionListener());

        panel.add(clientIdLabel);
        panel.add(clientIdField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Пустая метка для выравнивания
        panel.add(loginButton);

        frame.add(panel);
    }

    // Логика для входа и проверки роли пользователя
    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String clientId = clientIdField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (clientId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Client ID and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Проверка пользователя и его роли
            if (authenticate(clientId, password)) {
                // Проверка роли пользователя и переход к соответствующему интерфейсу
                String role = getRoleForUser(Integer.parseInt(clientId));

                switch (role) {
                    case "Client":
                        new ClientDashboard(Integer.parseInt(clientId)); // Переход для клиента
                        break;
                    case "Manager":
                        ManagerDashboard.showStartPage(); // Переход для менеджера
                        break;
                    case "Admin":
                        AdminDashboard.showStartPage(); // Переход для администратора
                        break;
                    default:
                        JOptionPane.showMessageDialog(frame, "Unknown role", "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                }
                frame.dispose(); // Закрыть окно входа
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Client ID or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Аутентификация пользователя
    private boolean authenticate(String clientId, String password) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Client.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            Client account = (Client) session.createQuery("FROM Client WHERE id=:clientId AND password=:password")
                    .setParameter("clientId", Integer.parseInt(clientId))
                    .setParameter("password", password)
                    .uniqueResult();

            return account != null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            factory.close();
        }
        return false;
    }

    // Получить роль пользователя
    private String getRoleForUser(int clientId) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Client.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            Client account = (Client) session.createQuery("FROM Client WHERE id=:clientId")
                    .setParameter("clientId", clientId)
                    .uniqueResult();

            if (account != null) {
                return account.getRole(); // Возвращаем роль
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            factory.close();
        }
        return null;
    }
}
