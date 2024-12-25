package com.warehouse.ui.managerPages;

import com.warehouse.dao.ContractDAO;
import com.warehouse.models.Contract;
import com.warehouse.models.Employee;
import com.warehouse.ui.adminPages.AdminMainPage;
import com.warehouse.ui.dialog.EditContractDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ContractPage extends JFrame {

    private JTable contractTable;
    private JButton backButton, editButton, deleteButton;
    private DefaultTableModel tableModel;

    public ContractPage() {
        setTitle("Contract Management");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Инициализация модели таблицы
        String[] columnNames = {"Contract Number", "Employee Login", "Start Date", "End Date", "Position", "Salary"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки таблицы не редактируемы
            }
        };

        contractTable = new JTable(tableModel);

        // Кнопки
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Назад");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        toolbar.add(backButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);

        // Добавляем таблицу и кнопки
        add(new JScrollPane(contractTable), BorderLayout.CENTER);
        add(toolbar, BorderLayout.NORTH);

        // Логика кнопок
        editButton.addActionListener(e -> editContract());
        deleteButton.addActionListener(e -> deleteContract());
        backButton.addActionListener(e -> {
            dispose(); // Закрываем текущее окно
            new ManagerMainPage(); // Возвращаемся на главную страницу
        });

        // Заполнение таблицы
        refreshTable();
    }

    private void refreshTable() {
        // Очистка таблицы
        tableModel.setRowCount(0);

        // Получение контрактов из базы данных
        List<Contract> contracts = ContractDAO.findAll();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Добавление строк в таблицу
        for (Contract contract : contracts) {
            // Если endDate пустой (null), используем строку "Not specified"
            String endDate = (contract.getEndDate() != null) ? dateFormat.format(contract.getEndDate()) : "Not specified";

            // Получаем логин сотрудника, связанного с контрактом
            Employee linkedEmployee = ContractDAO.getEmployeeByContract(contract.getContractNumber());
            String employeeLogin = (linkedEmployee != null) ? linkedEmployee.getEmployeeLogin() : "Not assigned";

            tableModel.addRow(new Object[]{
                    contract.getContractNumber(),
                    employeeLogin,
                    dateFormat.format(contract.getStartDate()),
                    endDate,
                    contract.getPosition(),
                    contract.getSalary()
            });
        }
    }

    private void editContract() {
        int selectedRow = contractTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a contract to edit.");
            return;
        }

        // Получение данных выбранного контракта
        int contractNumber = (int) tableModel.getValueAt(selectedRow, 0);
        Contract contract = ContractDAO.findById(contractNumber);

        // Открытие диалога редактирования
        new EditContractDialog(this, contract).setVisible(true);
        refreshTable();
    }

    private void deleteContract() {
        int selectedRow = contractTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a contract to delete.");
            return;
        }

        // Получение данных выбранного контракта
        int contractNumber = (int) tableModel.getValueAt(selectedRow, 0);

        // Проверяем, привязан ли сотрудник
        Employee linkedEmployee = ContractDAO.getEmployeeByContract(contractNumber);
        if (linkedEmployee != null) {
            JOptionPane.showMessageDialog(this, "Cannot delete contract. It is linked to an employee.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this contract?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ContractDAO.deleteContractById(contractNumber);
            JOptionPane.showMessageDialog(this, "Contract deleted successfully.");
            refreshTable();
        }
    }
}
