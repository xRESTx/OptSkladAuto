package com.warehouse.page.adminPage;

import com.warehouse.entities.Account;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
public class AccountPage {

    public static void showAccountPage() {
        JFrame frame = new JFrame("Account Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Account List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных аккаунтов
        String[] columnNames = {"ID", "Username", "Role", "Creation Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable accountTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(accountTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления аккаунта
        JButton addButton = new JButton("Add Account");
        addButton.addActionListener(e -> addAccount(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования аккаунта
        JButton editButton = new JButton("Edit Account");
        editButton.addActionListener(e -> editAccount(accountTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления аккаунта
        JButton deleteButton = new JButton("Delete Account");
        deleteButton.addActionListener(e -> deleteAccount(accountTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadAccountData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadAccountData(DefaultTableModel tableModel) {
        // Создаем сессию Hibernate для получения данных
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml") // Укажите путь к вашему файлу конфигурации Hibernate
                .addAnnotatedClass(Account.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех аккаунтов из базы данных
            List<Account> accounts = session.createQuery("from Account", Account.class).list();

            // Добавление данных в таблицу
            for (Account account : accounts) {
                tableModel.addRow(new Object[]{
                        account.getId(),
                        account.getUsername(),
                        account.getRole(),
                        account.getCreationDate()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading accounts: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            factory.close();
        }
    }

    private static void addAccount(DefaultTableModel tableModel) {
        JTextField usernameField = new JTextField();
        JTextField roleField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Role:"));
        panel.add(roleField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Account", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String role = roleField.getText();
            String password = new String(passwordField.getPassword());

            // Добавление аккаунта в базу данных
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Account.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Account account = new Account(username, password, role, LocalDate.now());
                session.save(account);

                session.getTransaction().commit();

                // Обновление таблицы
                tableModel.addRow(new Object[]{
                        account.getId(),
                        account.getUsername(),
                        account.getRole(),
                        account.getCreationDate()
                });

                JOptionPane.showMessageDialog(null, "Account added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding account: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }

    private static void editAccount(JTable accountTable, DefaultTableModel tableModel) {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an account to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int accountId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentUsername = (String) tableModel.getValueAt(selectedRow, 1);
        String currentRole = (String) tableModel.getValueAt(selectedRow, 2);

        JTextField usernameField = new JTextField(currentUsername);

        // Создаем JComboBox для выбора роли
        String[] roles = {"admin", "manager", "employee", "client"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        roleComboBox.setSelectedItem(currentRole);  // Устанавливаем текущую роль

        // Создаем панель с полями
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);

        // Окно подтверждения с редактированием
        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Account", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newUsername = usernameField.getText();
            String newRole = (String) roleComboBox.getSelectedItem();  // Получаем выбранную роль

            // Обновление аккаунта в базе данных
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Account.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Account account = session.get(Account.class, accountId);
                if (account != null) {
                    account.setUsername(newUsername);
                    account.setRole(newRole);
                    session.update(account);

                    session.getTransaction().commit();

                    // Обновление таблицы
                    tableModel.setValueAt(newUsername, selectedRow, 1);
                    tableModel.setValueAt(newRole, selectedRow, 2);

                    JOptionPane.showMessageDialog(null, "Account updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating account: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }

    private static void deleteAccount(JTable accountTable, DefaultTableModel tableModel) {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an account to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int accountId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this account?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Удаление аккаунта из базы данных
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Account.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Account account = session.get(Account.class, accountId);
                if (account != null) {
                    session.delete(account);
                    session.getTransaction().commit();

                    // Обновление таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Account deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting account: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }
}
