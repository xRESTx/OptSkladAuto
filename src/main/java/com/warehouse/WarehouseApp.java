package com.warehouse;

import com.warehouse.entities.Account;
import com.warehouse.dao.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WarehouseApp {

    private AccountDAO accountDAO;

    // Main window components
    private JFrame frame;
    private JTable accountTable;
    private DefaultTableModel tableModel;

    public WarehouseApp() {
        accountDAO = new AccountDAO();
        frame = new JFrame("Account Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        // Panel for buttons
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // Buttons
        JButton btnAdd = new JButton("Add Account");
        JButton btnUpdate = new JButton("Update Account");
        JButton btnDelete = new JButton("Delete Account");
        JButton btnViewAll = new JButton("View All Accounts");

        // Add buttons to the panel
        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnViewAll);

        // Add panel to the window
        frame.getContentPane().add(panel, BorderLayout.NORTH);

        // Table to display accounts
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"ID", "Username", "Password", "Role", "Creation Date"});
        accountTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(accountTable);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Button listeners
        btnAdd.addActionListener(e -> addAccount());
        btnUpdate.addActionListener(e -> updateAccount());
        btnDelete.addActionListener(e -> deleteAccount());
        btnViewAll.addActionListener(e -> loadAllAccounts());

        frame.setVisible(true);
    }

    // Method to load all accounts into the table
    private void loadAllAccounts() {
        List<Account> accounts = accountDAO.findAll();
        tableModel.setRowCount(0); // Clear the table before loading new data

        for (Account account : accounts) {
            tableModel.addRow(new Object[]{account.getId(), account.getUsername(), account.getPassword(),
                    account.getRole(), account.getCreationDate()});
        }
    }

    private void addAccount() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField roleField = new JTextField();

        // JDateChooser for selecting the date
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleField);
        panel.add(new JLabel("Creation Date:"));
        panel.add(dateChooser);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Add Account", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = roleField.getText();

            // Get the selected date from the JDateChooser
            java.util.Date selectedDate = dateChooser.getDate();
            if (selectedDate != null) {
                LocalDate creationDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();
                AccountDAO accountDAO = new AccountDAO();

                Account account = new Account(username, password, role, creationDate);
                accountDAO.save(account);
                loadAllAccounts();

            } else {
                JOptionPane.showMessageDialog(frame, "Please select a valid date.");
            }
        }
    }

    private void updateAccount() {
        int row = accountTable.getSelectedRow();
        if (row != -1) {
            Long id = (Long) accountTable.getValueAt(row, 0);
            Account account = accountDAO.findById(id.intValue());

            JPanel panel = new JPanel(new GridLayout(0, 2));

            JTextField usernameField = new JTextField(account.getUsername());
            JTextField passwordField = new JTextField(account.getPassword());
            JTextField roleField = new JTextField(account.getRole());
            JTextField creationDateField = new JTextField(account.getCreationDate().toString());

            panel.add(new JLabel("Username:"));
            panel.add(usernameField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);
            panel.add(new JLabel("Role:"));
            panel.add(roleField);
            panel.add(new JLabel("Creation Date (YYYY-MM-DD):"));
            panel.add(creationDateField);

            int option = JOptionPane.showConfirmDialog(frame, panel, "Update Account", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                account.setUsername(usernameField.getText());
                account.setPassword(passwordField.getText());
                account.setRole(roleField.getText());
                account.setCreationDate(LocalDate.parse(creationDateField.getText()));

                accountDAO.update(account);
                loadAllAccounts();
            }
        }
    }

    private void deleteAccount() {
        int row = accountTable.getSelectedRow();
        if (row != -1) {
            Long id = (Long) accountTable.getValueAt(row, 0);
            Account account = accountDAO.findById(id.intValue());
            accountDAO.delete(account);
            loadAllAccounts();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WarehouseApp::new);
    }
}
