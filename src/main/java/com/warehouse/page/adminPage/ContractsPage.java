package com.warehouse.page.adminPage;

import com.toedter.calendar.JDateChooser;
import com.warehouse.entities.Contract;
import com.warehouse.entities.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ContractsPage {

    public static void showContractsPage() {
        JFrame frame = new JFrame("Contract Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Заголовок
        JLabel titleLabel = new JLabel("Contract List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица для отображения данных контрактов
        String[] columnNames = {"ID", "Employee ID", "Contract Number", "Signing Date", "Salary"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable contractTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(contractTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();

        // Кнопка добавления контракта
        JButton addButton = new JButton("Add Contract");
        addButton.addActionListener(e -> addContract(tableModel));
        buttonPanel.add(addButton);

        // Кнопка редактирования контракта
        JButton editButton = new JButton("Edit Contract");
        editButton.addActionListener(e -> editContract(contractTable, tableModel));
        buttonPanel.add(editButton);

        // Кнопка удаления контракта
        JButton deleteButton = new JButton("Delete Contract");
        deleteButton.addActionListener(e -> deleteContract(contractTable, tableModel));
        buttonPanel.add(deleteButton);

        // Кнопка для закрытия окна
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных из базы данных
        loadContractData(tableModel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void loadContractData(DefaultTableModel tableModel) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Contract.class)
                .buildSessionFactory();

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            // Получение всех контрактов из базы данных
            List<Contract> contracts = session.createQuery("from Contract", Contract.class).list();

            // Добавление данных в таблицу
            for (Contract contract : contracts) {
                tableModel.addRow(new Object[]{
                        contract.getId(),
                        contract.getEmployee().getId(),
                        contract.getContractNumber(),
                        contract.getSigningDate(),
                        contract.getSalary()
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading contracts: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            factory.close();
        }
    }

    private static void addContract(DefaultTableModel tableModel) {
        JTextField employeeIdField = new JTextField();
        JTextField contractNumberField = new JTextField();
        JTextField salaryField = new JTextField();

        // Создаем JDateChooser для выбора даты
        JDateChooser signingDateChooser = new JDateChooser();
        signingDateChooser.setDateFormatString("yyyy-MM-dd"); // Формат отображения даты

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Employee ID:"));
        panel.add(employeeIdField);
        panel.add(new JLabel("Contract Number:"));
        panel.add(contractNumberField);
        panel.add(new JLabel("Signing Date:"));
        panel.add(signingDateChooser); // Добавляем JDateChooser в панель
        panel.add(new JLabel("Salary:"));
        panel.add(salaryField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Contract", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int employeeId = Integer.parseInt(employeeIdField.getText());
            String contractNumber = contractNumberField.getText();
            // Получаем дату из JDateChooser
            java.util.Date utilDate = signingDateChooser.getDate();
            if (utilDate != null) {
                // Преобразуем util.Date в LocalDate
                LocalDate signingDate = LocalDate.ofInstant(utilDate.toInstant(), java.time.ZoneId.systemDefault());
                int salary = Integer.parseInt(salaryField.getText());

                SessionFactory factory = new Configuration()
                        .configure("hibernate.cfg.xml")
                        .addAnnotatedClass(Contract.class)
                        .buildSessionFactory();

                try (Session session = factory.openSession()) {
                    session.beginTransaction();

                    Contract contract = new Contract();
                    contract.setEmployee(session.get(Employee.class, employeeId));
                    contract.setContractNumber(contractNumber);
                    contract.setSigningDate(signingDate);
                    contract.setSalary(salary);

                    session.save(contract);
                    session.getTransaction().commit();

                    // Обновляем таблицу
                    tableModel.addRow(new Object[]{
                            contract.getId(),
                            employeeId,
                            contract.getContractNumber(),
                            contract.getSigningDate(),
                            contract.getSalary()
                    });

                    JOptionPane.showMessageDialog(null, "Contract added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding contract: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    factory.close();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a signing date.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private static void editContract(JTable contractTable, DefaultTableModel tableModel) {
        int selectedRow = contractTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a contract to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int contractId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentContractNumber = (String) tableModel.getValueAt(selectedRow, 2);
        LocalDate currentSigningDate = (LocalDate) tableModel.getValueAt(selectedRow, 3);
        int currentSalary = (int) tableModel.getValueAt(selectedRow, 4);

        JTextField contractNumberField = new JTextField(currentContractNumber);
        JTextField signingDateField = new JTextField(currentSigningDate.toString());
        JTextField salaryField = new JTextField(String.valueOf(currentSalary));

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Contract Number:"));
        panel.add(contractNumberField);
        panel.add(new JLabel("Signing Date (YYYY-MM-DD):"));
        panel.add(signingDateField);
        panel.add(new JLabel("Salary:"));
        panel.add(salaryField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Contract", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newContractNumber = contractNumberField.getText();
            LocalDate newSigningDate = LocalDate.parse(signingDateField.getText());
            int newSalary = Integer.parseInt(salaryField.getText());

            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Contract.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Contract contract = session.get(Contract.class, contractId);
                if (contract != null) {
                    contract.setContractNumber(newContractNumber);
                    contract.setSigningDate(newSigningDate);
                    contract.setSalary(newSalary);
                    session.update(contract);

                    session.getTransaction().commit();

                    tableModel.setValueAt(newContractNumber, selectedRow, 2);
                    tableModel.setValueAt(newSigningDate, selectedRow, 3);
                    tableModel.setValueAt(newSalary, selectedRow, 4);

                    JOptionPane.showMessageDialog(null, "Contract updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Contract not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating contract: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }

    private static void deleteContract(JTable contractTable, DefaultTableModel tableModel) {
        int selectedRow = contractTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a contract to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int contractId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this contract?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Contract.class)
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Contract contract = session.get(Contract.class, contractId);
                if (contract != null) {
                    session.delete(contract);
                    session.getTransaction().commit();

                    // Обновление таблицы после удаления контракта
                    tableModel.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(null, "Contract deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Contract not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting contract: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                factory.close();
            }
        }
    }
}
