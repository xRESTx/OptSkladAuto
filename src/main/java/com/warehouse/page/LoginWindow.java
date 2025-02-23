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
    private JTextField usernameField;
    private JPasswordField passwordField;
    private static final SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Client.class)
            .buildSessionFactory();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginWindow window = new LoginWindow();
            window.frame.setVisible(true);
        });
    }

    public LoginWindow() {
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginActionListener());

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        frame.add(panel);
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Client client = authenticate(username, password);
            if (client != null) {
                switch (client.getRole()) {
                    case "Client":
                        new ClientDashboard(client.getId());
                        break;
                    case "Manager":
                        ManagerDashboard.showStartPage();
                        break;
                    case "Admin":
                        AdminDashboard.showStartPage();
                        break;
                    default:
                        JOptionPane.showMessageDialog(frame, "Unknown role", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                }
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Client authenticate(String username, String password) {
        try (Session session = factory.openSession()) {
            Client client = (Client) session.createQuery("FROM Client WHERE username = :username AND password = :password")
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();

            return client;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}