package com.warehouse.page.adminPage;

import com.toedter.calendar.JDateChooser;
import com.warehouse.entities.Employee;
import com.warehouse.entities.Account;
import com.warehouse.entities.Position;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class EmployeesPage {

    public static void showEmployeesPage() {
        JFrame frame = new JFrame("Employee Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Employee List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных сотрудников
        String[] columnNames = {"ID", "Full Name", "Phone Number", "Address", "Start Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable employeeTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления сотрудника
        JButton addButton = new JButton("Add Employee");
        addButton.addActionListener(e -> addEmployee(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования сотрудника
        JButton editButton = new JButton("Edit Employee");
        editButton.addActionListener(e -> editEmployee(employeeTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления сотрудника
        JButton deleteButton = new JButton("Delete Employee");
        deleteButton.addActionListener(e -> deleteEmployee(employeeTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadEmployeeData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadEmployeeData(DefaultTableModel tableModel) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Employee.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех сотрудников из базы данных
            List<Employee> employees = session.createQuery("from Employee", Employee.class).list();

            // Добавление данных в таблицу
            for (Employee employee : employees) {
                tableModel.addRow(new Object[] {
                        employee.getId(),
                        employee.getFullName(),
                        employee.getPhoneNumber(),
                        employee.getAddress(),
                        employee.getStartDate()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading employees: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addEmployee(DefaultTableModel tableModel) {
        // Получаем список всех аккаунтов, которых нет в таблицах Employees или Clients
        List<Account> availableAccounts = getAvailableAccounts();
        List<String> availableAccountsName = new ArrayList<>();
        for (Account account : availableAccounts) {
            availableAccountsName.add(account.getUsername());
        }

        // Получаем список всех должностей
        List<Position> availablePositions = getAvailablePositions();

        JTextField fullNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();

        // Создаем комбобоксы для выбора аккаунта и должности
        JComboBox<String> accountComboBox = new JComboBox<>(availableAccountsName.toArray(new String[0]));
        JComboBox<Position> positionComboBox = new JComboBox<>(availablePositions.toArray(new Position[0]));

        // Используем JDateChooser для выбора даты
        JDateChooser startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd"); // Формат отображения даты

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Start Date:"));
        panel.add(startDateChooser);
        panel.add(new JLabel("Account:"));
        panel.add(accountComboBox);
        panel.add(new JLabel("Position:"));
        panel.add(positionComboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String fullName = fullNameField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();

            // Получаем выбранный аккаунт по имени
            String selectedUsername = (String) accountComboBox.getSelectedItem();
            Account selectedAccount = availableAccounts.stream()
                    .filter(account -> account.getUsername().equals(selectedUsername))
                    .findFirst()
                    .orElse(null);

            Position selectedPosition = (Position) positionComboBox.getSelectedItem();

            if (selectedAccount == null || selectedPosition == null) {
                JOptionPane.showMessageDialog(null, "Please select both a valid account and position.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Получаем выбранную дату начала работы (если дата не выбрана, будет null)
            java.util.Date startDateUtil = startDateChooser.getDate();
            if (startDateUtil == null) {
                JOptionPane.showMessageDialog(null, "Please select a valid start date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate startDate = startDateUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Настройка Hibernate
            try (SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Employee.class)
                    .buildSessionFactory();
                 Session session = factory.openSession()) {

                session.beginTransaction();

                // Создание нового сотрудника
                Employee employee = new Employee(fullName, phone, address, startDate, selectedPosition, selectedAccount);
                session.save(employee);
                session.getTransaction().commit();

                // Добавление нового сотрудника в таблицу
                tableModel.addRow(new Object[]{
                        employee.getId(),
                        employee.getFullName(),
                        employee.getPhoneNumber(),
                        employee.getAddress(),
                        employee.getStartDate()
                });

                // Уведомление об успешном добавлении
                JOptionPane.showMessageDialog(null, "Employee added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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

    private static List<Position> getAvailablePositions() {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Position.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            List<Position> positions = session.createQuery("from Position", Position.class).list();
            session.getTransaction().commit();
            return positions;
        }
    }

    private static void editEmployee(JTable employeeTable, DefaultTableModel tableModel) {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an employee to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentFullName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentPhone = (String) tableModel.getValueAt(selectedRow, 2);
        String currentAddress = (String) tableModel.getValueAt(selectedRow, 3);
        String currentStartDate = tableModel.getValueAt(selectedRow, 4).toString();

        JTextField fullNameField = new JTextField(currentFullName);
        JTextField phoneField = new JTextField(currentPhone);
        JTextField addressField = new JTextField(currentAddress);
        JTextField startDateField = new JTextField(currentStartDate);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        panel.add(startDateField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newFullName = fullNameField.getText();
            String newPhone = phoneField.getText();
            String newAddress = addressField.getText();
            String newStartDate = startDateField.getText();

            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Employee.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Employee employee = session.get(Employee.class, employeeId);
                employee.setFullName(newFullName);
                employee.setPhoneNumber(newPhone);
                employee.setAddress(newAddress);
                employee.setStartDate(LocalDate.parse(newStartDate));

                session.update(employee);
                session.getTransaction().commit();

                tableModel.setValueAt(newFullName, selectedRow, 1);
                tableModel.setValueAt(newPhone, selectedRow, 2);
                tableModel.setValueAt(newAddress, selectedRow, 3);
                tableModel.setValueAt(newStartDate, selectedRow, 4);

                JOptionPane.showMessageDialog(null, "Employee updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void deleteEmployee(JTable employeeTable, DefaultTableModel tableModel) {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an employee to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this employee?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Employee.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Employee employee = session.get(Employee.class, employeeId);
                if (employee != null) {
                    session.delete(employee);
                    session.getTransaction().commit();

                    // Удаление строки из таблицы
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Employee deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
