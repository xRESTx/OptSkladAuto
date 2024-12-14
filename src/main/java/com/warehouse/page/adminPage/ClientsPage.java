package com.warehouse.page.adminPage;

import com.warehouse.entities.Account;
import com.warehouse.entities.Client;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClientsPage {

    public static void showClientsPage() {
        JFrame frame = new JFrame("Client Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Client List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных клиентов
        String[] columnNames = {"ID", "Full Name", "Phone Number", "Email", "Address"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable clientTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(clientTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления клиента
        JButton addButton = new JButton("Add Client");
        addButton.addActionListener(e -> addClient(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования клиента
        JButton editButton = new JButton("Edit Client");
        editButton.addActionListener(e -> editClient(clientTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления клиента
        JButton deleteButton = new JButton("Delete Client");
        deleteButton.addActionListener(e -> deleteClient(clientTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadClientData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadClientData(DefaultTableModel tableModel) {
        // Создаем сессию Hibernate для получения данных
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Client.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех клиентов из базы данных
            List<Client> clients = session.createQuery("from Client", Client.class).list();

            // Добавление данных в таблицу
            for (Client client : clients) {
                tableModel.addRow(new Object[]{
                        client.getClientId(),
                        client.getFullName(),
                        client.getPhoneNumber(),
                        client.getEmail(),
                        client.getAddress()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading clients: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            factory.close();
        }
    }

    private static List<Account> getAvailableAccounts() {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Account.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            List<Account> allAccounts = session.createQuery("from Account", Account.class).list();

            // Получаем список аккаунтов, которые еще не зарегистрированы как сотрудники или клиенты
            List<Account> availableAccounts = session.createQuery(
                            "select a from Account a where a.id not in (select e.account.id from Employee e) " +
                                    "and a.id not in (select c.account.id from Client c)", Account.class)
                    .getResultList();

            session.getTransaction().commit();
            return availableAccounts;
        }
    }

    private static void addClient(DefaultTableModel tableModel) {
        // Получаем список всех аккаунтов, которых нет в таблицах Employees или Clients
        List<Account> availableAccounts = getAvailableAccounts();
        List<String> availableAccountsName = new ArrayList<>();
        for (Account account : availableAccounts) {
            availableAccountsName.add(account.getUsername());
        }

        JTextField fullNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();

        JComboBox<String> accountComboBox = new JComboBox<>(availableAccountsName.toArray(new String[0]));

        JPanel panel = new JPanel(new GridLayout(5, 2));  // Увеличиваем количество строк для добавления поля "Account"
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Account:"));
        panel.add(accountComboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Client", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String fullName = fullNameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();

            // Проверка на пустые поля
            if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled out.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Получаем выбранный аккаунт из JComboBox
            String selectedAccountName = (String) accountComboBox.getSelectedItem();
            Account selectedAccount = null;

            // Находим аккаунт по имени
            for (Account account : availableAccounts) {
                if (account.getUsername().equals(selectedAccountName)) {
                    selectedAccount = account;
                    break;
                }
            }

            // Если аккаунт найден, то создаем клиента
            if (selectedAccount != null) {
                SessionFactory factory = new Configuration()
                        .configure("hibernate.cfg.xml")
                        .addAnnotatedClass(Client.class)
                        .buildSessionFactory();

                try (Session session = factory.openSession()) {
                    session.beginTransaction();

                    // Создаем нового клиента с выбранным аккаунтом
                    Client client = new Client(fullName, phone, email, address, selectedAccount);
                    session.save(client);

                    session.getTransaction().commit();

                    // Добавляем нового клиента в таблицу
                    tableModel.addRow(new Object[]{
                            client.getClientId(),
                            client.getFullName(),
                            client.getPhoneNumber(),
                            client.getEmail(),
                            client.getAddress()
                    });

                    JOptionPane.showMessageDialog(null, "Client added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding client: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    factory.close();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selected account not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void deleteClient(JTable clientTable, DefaultTableModel tableModel) {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a client to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int clientId = (int) tableModel.getValueAt(selectedRow, 0);

        // Запрос подтверждения удаления
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this client?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Client.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Client client = session.get(Client.class, clientId);
                if (client != null) {
                    session.delete(client);  // Удаление клиента из базы данных
                    session.getTransaction().commit();

                    // Удаляем клиента из таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Client deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Client not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting client: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }

    private static void editClient(JTable clientTable, DefaultTableModel tableModel) {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a client to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int clientId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentFullName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentPhone = (String) tableModel.getValueAt(selectedRow, 2);
        String currentEmail = (String) tableModel.getValueAt(selectedRow, 3);
        String currentAddress = (String) tableModel.getValueAt(selectedRow, 4);

        JTextField fullNameField = new JTextField(currentFullName);
        JTextField phoneField = new JTextField(currentPhone);
        JTextField emailField = new JTextField(currentEmail);
        JTextField addressField = new JTextField(currentAddress);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Client", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newFullName = fullNameField.getText();
            String newPhone = phoneField.getText();
            String newEmail = emailField.getText();
            String newAddress = addressField.getText();

            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Client.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Client client = session.get(Client.class, clientId);
                if (client != null) {
                    client.setFullName(newFullName);
                    client.setPhoneNumber(newPhone);
                    client.setEmail(newEmail);
                    client.setAddress(newAddress);
                    session.update(client);

                    session.getTransaction().commit();

                    tableModel.setValueAt(newFullName, selectedRow, 1);
                    tableModel.setValueAt(newPhone, selectedRow, 2);
                    tableModel.setValueAt(newEmail, selectedRow, 3);
                    tableModel.setValueAt(newAddress, selectedRow, 4);

                    JOptionPane.showMessageDialog(null, "Client updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Client not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error editing client: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }
}
